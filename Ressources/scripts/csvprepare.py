import pandas as pd
import chardet
import os
import numpy as np
import json

def detect_file_encoding(file_path):
    # Detect file encoding
    with open(file_path, 'rb') as file:
        raw_data = file.read()
        result = chardet.detect(raw_data)
        print(f"Encodage détecté : {result}")
        return result['encoding']

try:
    # Define base directory and file paths
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    file_path = os.path.join(base_dir, "Fichiers .csv", "Originaux", "2023.csv")
    output_dir = os.path.join(base_dir, "Fichiers .csv", "Tests")
    
    # Ensure output directory exists
    os.makedirs(output_dir, exist_ok=True)
    
    encoding = detect_file_encoding(file_path)
    
    # Read the file using line 3 as header (index 2)
    data = pd.read_csv(file_path, 
                      delimiter=';',
                      encoding=encoding,
                      header=2,
                      dtype={'Num client': str, 'Paire': str},
                      on_bad_lines='warn')
    
    print(f"\nNombre de lignes lues : {len(data)}")
    print(f"Colonnes trouvées : {list(data.columns)}")

    # Delete specified columns
    columns_to_delete = [
        "Code article", "Ref. Mag.", "Société", "Libellé", 
        "Taille", "Couleur", "Matière", "Type de Verre", 
        "Genre", "Marque", "Fournisseur", "Code Barre", 
        "Gamme", "Prix Achat", "Marge", "MAJ Stock"
    ]
    data = data.drop(columns=columns_to_delete, errors='ignore')

    # Identify clients to consider, excluding "PASSAGE passage"
    clients_to_consider = data['Client'].unique()
    clients_to_consider = [c for c in clients_to_consider if c != "PASSAGE passage"]

    # Randomly select about 50% of clients
    num_clients_to_remove = int(len(clients_to_consider) * 0.5)
    clients_to_remove = set(pd.Series(clients_to_consider).sample(num_clients_to_remove, random_state=42))

    # Filter rows
    data_filtered = data[~data['Client'].isin(clients_to_remove)]

    # List of possible vendors
    vendors = ['irau', 'bstud', 'hiskander', 'egaligai']
    
    # Create a dictionary to map each unique client to a random vendor
    unique_clients = data_filtered['Client'].unique()
    client_vendor_mapping = {
        client: np.random.choice(vendors) 
        for client in unique_clients if client != "PASSAGE passage"
    }
    
    # Apply the mapping to replace RefVendeur values for non-PASSAGE clients
    data_filtered['RefVendeur'] = data_filtered.apply(
        lambda row: np.random.choice(vendors) if row['Client'] == "PASSAGE passage"
        else client_vendor_mapping[row['Client']], 
        axis=1
    )
    
    print("\nDistribution des vendeurs après remplacement :")
    print(data_filtered['RefVendeur'].value_counts())
    
    # Vérification pour PASSAGE passage
    if "PASSAGE passage" in data_filtered['Client'].unique():

        # Load JSON data
        json_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "nomprenom.json")
        with open(json_path, 'r', encoding='utf-8') as f:
            name_data = json.loads(f.read())
        
        # Get unique clients (excluding "PASSAGE passage")
        unique_clients = data_filtered['Client'].unique()
        unique_clients = [c for c in unique_clients if c != "PASSAGE passage"]
        
        # Create random full names
        full_names = [
            f"{np.random.choice(name_data['last_names'])} {np.random.choice(name_data['first_names'])}"
            for _ in range(len(unique_clients))
        ]
        
        # Create mapping dictionary
        client_name_mapping = dict(zip(unique_clients, full_names))
        
        # Add "PASSAGE passage" back if it exists
        if "PASSAGE passage" in data_filtered['Client'].unique():
            client_name_mapping["PASSAGE passage"] = "PASSAGE passage"
        
        # Apply the mapping to replace Client values
        data_filtered['Client'] = data_filtered['Client'].map(client_name_mapping)

    # Save modified file
    output_path = os.path.join(output_dir, "test_2023.csv")
    data_filtered.to_csv(output_path, index=False, sep=';', encoding='utf-8-sig')

except Exception as e:
    print(f"Error: {str(e)}")
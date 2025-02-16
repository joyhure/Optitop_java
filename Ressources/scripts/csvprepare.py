import pandas as pd
import chardet

def detect_file_encoding(file_path):
    # Détecter l'encodage du fichier
    with open(file_path, 'rb') as file:
        raw_data = file.read()
        result = chardet.detect(raw_data)
        print(f"Encodage détecté : {result}")
        return result['encoding']

try:
    file_path = "2023.csv"
    encoding = detect_file_encoding(file_path)
    
    # Lire le fichier en utilisant la ligne 2 comme en-tête (index 1)
    data = pd.read_csv(file_path, 
                      delimiter=';',
                      encoding=encoding,
                      header=1,  # Ligne 2 comme en-tête
                      on_bad_lines='warn')
    
    print(f"\nNombre de lignes lues : {len(data)}")
    print(f"Colonnes trouvées : {list(data.columns)}")

    # Identifier les clients à considérer, en excluant "PASSAGE passage"
    clients_to_consider = data['Client'].unique()
    clients_to_consider = [c for c in clients_to_consider if c != "PASSAGE passage"]

    # Sélectionner aléatoirement environ 50 % des clients
    num_clients_to_remove = int(len(clients_to_consider) * 0.5)
    clients_to_remove = set(pd.Series(clients_to_consider).sample(num_clients_to_remove, random_state=42))

    # Filtrer les lignes
    data_filtered = data[~data['Client'].isin(clients_to_remove)]

    # Enregistrer le fichier modifié
    output_path = "Filtered_2023.csv"
    data_filtered.to_csv(output_path, index=False, sep=';', encoding='utf-8-sig')
    print(f"\nFichier sauvegardé sous : {output_path}")

except Exception as e:
    print(f"Erreur : {str(e)}")
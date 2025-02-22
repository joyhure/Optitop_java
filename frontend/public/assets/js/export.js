document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('importForm');
    const progressBar = document.querySelector('#uploadProgress .progress-bar');
    const progressDiv = document.getElementById('uploadProgress');
    const statusDiv = document.getElementById('importStatus');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const file = document.getElementById('csvFile').files[0];
        const chunkSize = 1024 * 1024; // 1MB
        const totalChunks = Math.ceil(file.size / chunkSize);
        let currentChunk = 0;

        progressDiv.classList.remove('d-none');
        statusDiv.classList.remove('d-none');
        statusDiv.className = 'alert alert-info';
        statusDiv.textContent = 'Import en cours...';

        const uploadChunk = async () => {
            const start = currentChunk * chunkSize;
            const end = Math.min(start + chunkSize, file.size);
            const chunk = file.slice(start, end);

            const formData = new FormData();
            formData.append('file', chunk);
            formData.append('chunk', currentChunk);
            formData.append('totalChunks', totalChunks);

            try {
                const response = await fetch('http://localhost:8080/api/sales/import', {
                    method: 'POST',
                    body: formData
                });

                if (response.ok) {
                    currentChunk++;
                    const progress = Math.round((currentChunk / totalChunks) * 100);
                    progressBar.style.width = `${progress}%`;
                    progressBar.textContent = `${progress}%`;

                    if (currentChunk < totalChunks) {
                        uploadChunk();
                    } else {
                        statusDiv.className = 'alert alert-success';
                        statusDiv.textContent = 'Import réussi !';
                        form.reset();
                    }
                } else {
                    throw new Error('Erreur serveur');
                }
            } catch (error) {
                statusDiv.className = 'alert alert-danger';
                statusDiv.textContent = 'Erreur lors de l\'import';
                console.error(error);
            }
        };

        uploadChunk();
    });
});
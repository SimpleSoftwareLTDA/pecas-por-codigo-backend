const API_BASE = 'https://backend.pecasporcodigo.com.br/api/v1';

// --- Feature Flags ---
const FLAGS = {
    ENABLE_LOGIN: false
};

// --- Auth Management ---
const loginScreen = document.getElementById('loginScreen');
const mainApp = document.getElementById('mainApp');
const loginTokenInput = document.getElementById('loginToken');
const loginBtn = document.getElementById('loginBtn');
const loginError = document.getElementById('loginError');
const logoutBtn = document.getElementById('logoutBtn');

let currentToken = localStorage.getItem('ppc_admin_token');

async function checkAuth() {
    if (!FLAGS.ENABLE_LOGIN) {
        hideLogin();
        fetchSuppliers();
        return;
    }

    if (!currentToken) {
        showLogin();
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/login/verify?token=${currentToken}`);
        if (response.ok) {
            hideLogin();
            fetchSuppliers();
        } else {
            localStorage.removeItem('ppc_admin_token');
            showLogin();
        }
    } catch (err) {
        console.error('Auth check failed:', err);
        showLogin();
    }
}

function showLogin() {
    loginScreen.classList.remove('hidden');
    mainApp.classList.add('hidden');
}

function hideLogin() {
    loginScreen.classList.add('hidden');
    mainApp.classList.remove('hidden');
}

loginBtn.onclick = async () => {
    const token = loginTokenInput.value.trim();
    if (!token) return;

    loginBtn.disabled = true;
    loginBtn.innerText = 'Verificando...';
    loginError.classList.add('hidden');

    try {
        const response = await fetch(`${API_BASE}/login/verify?token=${token}`);
        if (response.ok) {
            currentToken = token;
            localStorage.setItem('ppc_admin_token', token);
            hideLogin();
            fetchSuppliers();
        } else {
            loginError.classList.remove('hidden');
        }
    } catch (err) {
        console.error('Login error:', err);
        alert('Erro de conexão ao verificar token.');
    } finally {
        loginBtn.disabled = false;
        loginBtn.innerText = 'Acessar Painel';
    }
};

logoutBtn.onclick = () => {
    localStorage.removeItem('ppc_admin_token');
    currentToken = null;
    location.reload();
};

// Start by checking auth
checkAuth();

// --- UI Elements ---
const navLinks = document.querySelectorAll('.nav-link');
const tabContents = document.querySelectorAll('.tab-content');
const supplierSearch = document.getElementById('supplierSearch');
let allSuppliers = [];

// Banner UI Elements
const bannersList = document.getElementById('bannersList');
const bannersLoader = document.getElementById('bannersLoader');
const bannerCnpjInput = document.getElementById('bannerCnpj');
const bannerUrlInput = document.getElementById('bannerUrl');
const updateBannerBtn = document.getElementById('updateBannerBtn');
const bannerUpdateMessage = document.getElementById('bannerUpdateMessage');

navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const tabId = link.getAttribute('data-tab');

        navLinks.forEach(l => l.classList.remove('active'));
        tabContents.forEach(tc => tc.classList.remove('active'));

        link.classList.add('active');
        document.getElementById(tabId).classList.add('active');

        if (tabId === 'suppliers') fetchSuppliers();
        if (tabId === 'banners') fetchBanners();
    });
});

// Search functionality
supplierSearch.addEventListener('input', (e) => {
    const term = e.target.value.trim().toLowerCase();
    if (term.length === 0) {
        renderSuppliers(allSuppliers);
        return;
    }

    // If it looks like a CNPJ (mostly digits), we could call the backend
    // But for a fast UI, we filter locally first
    const filtered = allSuppliers.filter(s =>
        (s.empresa && s.empresa.toLowerCase().includes(term)) ||
        (s.cnpj && s.cnpj.includes(term)) ||
        (s.razaoSocial && s.razaoSocial.toLowerCase().includes(term))
    );
    renderSuppliers(filtered);
});

// Add Enter listener for CNPJ backend search (Main Key)
supplierSearch.addEventListener('keypress', async (e) => {
    if (e.key === 'Enter') {
        let cnpj = e.target.value.trim();
        // Clean non-digits for API call if needed, but let's keep it as is if backend handles it
        if (cnpj.length >= 14) {
            searchByCnpj(cnpj);
        }
    }
});

async function searchByCnpj(cnpj) {
    try {
        setLoader('tableLoader', true);
        // Ensure CNPJ is formatted or cleaned as per backend expectations
        const response = await fetch(`${API_BASE}/fornecedores/cnpj?cnpj=${encodeURIComponent(cnpj)}`);
        if (!response.ok) throw new Error('CNPJ search failed');
        const data = await response.json();
        // Backend returns Page object
        allSuppliers = data.content || (Array.isArray(data) ? data : [data]);
        renderSuppliers(allSuppliers);

        if (allSuppliers.length === 0) {
            alert('Nenhum fornecedor encontrado com este CNPJ.');
        }
    } catch (err) {
        console.error(err);
        alert('Erro ao buscar CNPJ. Verifique a conexão com o backend.');
    } finally {
        setLoader('tableLoader', false);
    }
}

// --- Suppliers CRUD ---
const supplierList = document.getElementById('supplierList');
const supplierForm = document.getElementById('supplierForm');
const supplierModal = document.getElementById('supplierModal');
const addSupplierBtn = document.getElementById('addSupplierBtn');
const closeModalBtns = document.querySelectorAll('.close-modal');
let editingSupplierId = null;

async function fetchSuppliers() {
    try {
        setLoader('tableLoader', true);
        const response = await fetch(`${API_BASE}/fornecedores?size=100`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const data = await response.json();
        allSuppliers = data.content || (Array.isArray(data) ? data : []);
        renderSuppliers(allSuppliers);
    } catch (err) {
        console.error('Error fetching suppliers:', err);
        supplierList.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--danger)">Erro ao carregar fornecedores. Verifique se o backend está rodando em :8080</td></tr>`;
    } finally {
        setLoader('tableLoader', false);
    }
}

function renderSuppliers(suppliers) {
    supplierList.innerHTML = '';
    suppliers.forEach(s => {
        const tr = document.createElement('tr');
        // Using s.name or s.nome depending on mapping
        const displayName = s.nome || s.name || s.empresa || 'N/A';
        const displaySocial = s.razaoSocial || s.socialName || 'N/A';

        const planName = s.idPlano == 2 ? 'VIP' : 'Básico';

        tr.innerHTML = `
            <td><strong>${displayName}</strong></td>
            <td>${s.cnpj || 'N/A'}</td>
            <td>${s.address?.cidade || s.endereco?.cidade || 'N/A'}</td>
            <td><span class="badge ${planName.toLowerCase()}">${planName}</span></td>
            <td>
                <div class="action-btns">
                    <button class="btn-icon" onclick="editSupplier(${s.id})"><i class="fas fa-edit"></i></button>
                    <button class="btn-icon delete" onclick="deleteSupplier(${s.id})"><i class="fas fa-trash"></i></button>
                </div>
            </td>
        `;
        supplierList.appendChild(tr);
    });
}

addSupplierBtn.onclick = () => {
    editingSupplierId = null;
    document.getElementById('modalTitle').innerText = 'Novo Fornecedor';
    supplierForm.reset();
    supplierModal.style.display = 'block';
};

closeModalBtns.forEach(btn => btn.onclick = () => supplierModal.style.display = 'none');

supplierForm.onsubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData(supplierForm);
    const payload = {
        empresa: formData.get('empresa'),
        razaoSocial: formData.get('razaoSocial'),
        cnpj: formData.get('cnpj'),
        inscricao: formData.get('inscricao'),
        idPlano: parseInt(formData.get('idPlano')),
        contato: {
            vendedores: formData.get('vendedores'),
            emailPecas: formData.get('emailPecas'),
            whatsappPecas: formData.get('whatsappPecas'),
            website: formData.get('website')
        },
        endereco: {
            endereco: formData.get('endereco'),
            cidade: formData.get('cidade'),
            cep: formData.get('cep'),
            idEstado: parseInt(formData.get('idEstado'))
        },
        // Requirements for Create/Update Aliases
        name: formData.get('empresa'),
        socialName: formData.get('razaoSocial')
    };

    try {
        const method = editingSupplierId ? 'PUT' : 'POST';
        const url = editingSupplierId ? `${API_BASE}/fornecedores/${editingSupplierId}` : `${API_BASE}/fornecedores`;

        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert('Fornecedor salvo com sucesso!');
            supplierModal.style.display = 'none';
            fetchSuppliers();
        } else {
            alert('Erro ao salvar fornecedor.');
        }
    } catch (err) {
        console.error(err);
    }
};

window.editSupplier = async (id) => {
    try {
        const response = await fetch(`${API_BASE}/fornecedores/${id}`);
        const s = await response.json();

        editingSupplierId = id;
        document.getElementById('modalTitle').innerText = 'Editar Fornecedor';

        // Fill form
        const f = supplierForm.elements;
        f['empresa'].value = s.nome || s.name || '';
        f['razaoSocial'].value = s.razaoSocial || s.socialName || '';
        f['cnpj'].value = s.cnpj || '';
        f['inscricao'].value = s.inscricaoEstadual || s.inscricao || '';
        f['vendedores'].value = s.contato?.vendedores || '';
        f['emailPecas'].value = s.contato?.emailPecas || '';
        f['whatsappPecas'].value = s.contato?.whatsappPecas || '';
        f['website'].value = s.contato?.website || '';

        // Address mapping (Backend uses 'endereco' which contains 'endereco' as street)
        const addr = s.endereco || s.address;
        f['endereco'].value = addr?.endereco || addr?.street || '';
        f['cidade'].value = addr?.cidade || addr?.city || '';
        f['cep'].value = addr?.cep || '';
        f['idEstado'].value = addr?.estado?.id || addr?.state?.id || 1;

        f['idPlano'].value = s.idPlano || 1;

        supplierModal.style.display = 'block';
    } catch (err) {
        console.error(err);
        alert('Erro ao carregar detalhes do fornecedor.');
    }
};

window.deleteSupplier = async (id) => {
    if (!confirm('Tem certeza que deseja excluir este fornecedor?')) return;
    try {
        const response = await fetch(`${API_BASE}/fornecedores/${id}`, { method: 'DELETE' });
        if (response.ok) {
            fetchSuppliers();
        }
    } catch (err) {
        console.error(err);
    }
};

// --- Contact Form ---
const contactForm = document.getElementById('contactForm');
contactForm.onsubmit = async (e) => {
    e.preventDefault();
    const payload = {
        name: document.getElementById('contactName').value,
        email: document.getElementById('contactEmail').value,
        subject: document.getElementById('contactSubject').value,
        message: document.getElementById('contactMessage').value
    };

    try {
        const response = await fetch(`${API_BASE}/contact-form`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (response.ok) {
            alert('Mensagem enviada com sucesso!');
            contactForm.reset();
        } else {
            alert('Erro ao enviar mensagem.');
        }
    } catch (err) {
        console.error(err);
    }
};

// --- Stock Upload ---
const stockFile = document.getElementById('stockFile');
const stockCnpjInput = document.getElementById('stockCnpj');
const uploadBtn = document.getElementById('uploadBtn');
const fileNameLabel = document.getElementById('fileName');
const filePreview = document.getElementById('filePreview');
const previewTableHead = document.getElementById('previewTableHead');
const previewTableBody = document.getElementById('previewTableBody');
const previewStats = document.getElementById('previewStats');
const validationMessages = document.getElementById('validationMessages');
const uploadHistoryList = document.getElementById('uploadHistoryList');

let currentFileData = null;
let uploadHistory = JSON.parse(localStorage.getItem('ppc_upload_history') || '[]');

// Template download
downloadTemplateBtn.onclick = () => {
    const template = `CODIGO\tQUANTIDADE\tPRECO\tDESCRICAO
ABC123\t10\t150.50\tPeça de exemplo 1
XYZ789\t5\t89.90\tPeça de exemplo 2
DEF456\t20\t45.00\tPeça de exemplo 3`;

    const blob = new Blob([template], { type: 'text/tab-separated-values' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'template_estoque.tsv';
    a.click();
    URL.revokeObjectURL(url);
};

// File validation and preview
async function validateAndPreviewFile(file) {
    const ext = file.name.split('.').pop().toLowerCase();
    if (ext !== 'tsv' && ext !== 'csv') {
        showValidationMessage('Apenas arquivos .tsv ou .csv são permitidos.', 'error');
        return false;
    }

    try {
        const text = await file.text();
        const delimiter = ext === 'tsv' ? '\t' : /[;\t]/;
        const lines = text.trim().split('\n').filter(line => line.trim());

        if (lines.length === 0) {
            showValidationMessage('Arquivo vazio.', 'error');
            return false;
        }

        const rows = lines.map(line => line.split(delimiter).map(cell => cell.trim()));

        // Validation
        const validationResults = [];
        let hasErrors = false;

        // Check column count consistency
        const expectedColumns = 4;
        const invalidRows = rows.filter(row => row.length !== expectedColumns);

        if (invalidRows.length > 0) {
            validationResults.push({
                type: 'error',
                message: `${invalidRows.length} linha(s) com número incorreto de colunas (esperado: ${expectedColumns})`
            });
            hasErrors = true;
        }

        // Check for required fields
        const emptyFields = rows.slice(1).filter(row =>
            !row[0] || !row[1] || !row[2] || !row[3]
        );

        if (emptyFields.length > 0) {
            validationResults.push({
                type: 'warning',
                message: `${emptyFields.length} linha(s) com campos vazios`
            });
        }

        // Check numeric fields
        const invalidQuantities = rows.slice(1).filter(row =>
            row[1] && isNaN(parseInt(row[1]))
        );

        if (invalidQuantities.length > 0) {
            validationResults.push({
                type: 'error',
                message: `${invalidQuantities.length} linha(s) com quantidade inválida`
            });
            hasErrors = true;
        }

        // Success message
        if (!hasErrors) {
            validationResults.push({
                type: 'success',
                message: `✓ Arquivo válido! ${rows.length - 1} linha(s) de dados encontradas.`
            });
        }

        // Display validation messages
        validationMessages.innerHTML = '';
        validationResults.forEach(result => {
            const div = document.createElement('div');
            div.className = `validation-message ${result.type}`;
            div.innerHTML = `<i class="fas fa-${result.type === 'error' ? 'exclamation-circle' : result.type === 'warning' ? 'exclamation-triangle' : 'check-circle'}"></i> ${result.message}`;
            validationMessages.appendChild(div);
        });

        // Show preview (first 10 rows)
        const previewRows = rows.slice(0, 11); // Header + 10 data rows

        // Build table header
        previewTableHead.innerHTML = `<tr>${previewRows[0].map(cell => `<th>${cell}</th>`).join('')}</tr>`;

        // Build table body
        previewTableBody.innerHTML = previewRows.slice(1).map(row =>
            `<tr>${row.map(cell => `<td>${cell}</td>`).join('')}</tr>`
        ).join('');

        // Show stats
        const totalRows = rows.length - 1;
        const showing = Math.min(10, totalRows);
        previewStats.textContent = `Exibindo ${showing} de ${totalRows} linha(s) de dados`;

        filePreview.classList.remove('hidden');
        currentFileData = { rows, totalRows, hasErrors };

        return !hasErrors;
    } catch (err) {
        console.error('Error parsing file:', err);
        showValidationMessage('Erro ao processar arquivo. Verifique o formato.', 'error');
        return false;
    }
}

function showValidationMessage(message, type) {
    validationMessages.innerHTML = `
        <div class="validation-message ${type}">
            <i class="fas fa-${type === 'error' ? 'exclamation-circle' : 'exclamation-triangle'}"></i>
            ${message}
        </div>
    `;
}

stockFile.onchange = async (e) => {
    if (e.target.files.length > 0) {
        const file = e.target.files[0];
        fileNameLabel.innerText = file.name;
        await validateAndPreviewFile(file);
    } else {
        filePreview.classList.add('hidden');
        currentFileData = null;
    }
};

// --- Drag and Drop ---
const dropZone = document.getElementById('dropZone');

dropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropZone.classList.add('dragover');
});

dropZone.addEventListener('dragleave', () => {
    dropZone.classList.remove('dragover');
});

dropZone.addEventListener('drop', async (e) => {
    e.preventDefault();
    dropZone.classList.remove('dragover');

    if (e.dataTransfer.files.length > 0) {
        const file = e.dataTransfer.files[0];
        stockFile.files = e.dataTransfer.files;
        fileNameLabel.innerText = file.name;
        await validateAndPreviewFile(file);
    }
});

// Upload History Management
function addToHistory(entry) {
    uploadHistory.unshift(entry);
    if (uploadHistory.length > 20) uploadHistory = uploadHistory.slice(0, 20);
    localStorage.setItem('ppc_upload_history', JSON.stringify(uploadHistory));
    renderUploadHistory();
}

function renderUploadHistory() {
    if (uploadHistory.length === 0) {
        uploadHistoryList.innerHTML = '<tr><td colspan="5" style="text-align:center; color:var(--text-muted)">Nenhum upload realizado ainda</td></tr>';
        return;
    }

    uploadHistoryList.innerHTML = uploadHistory.map(entry => {
        const statusClass = entry.status === 'completed' ? 'completed' : entry.status === 'error' ? 'error' : 'processing';
        return `
            <tr>
                <td>${new Date(entry.timestamp).toLocaleString('pt-BR')}</td>
                <td>${entry.fileName}</td>
                <td>${entry.cnpj}</td>
                <td><span class="status-badge ${statusClass}">${entry.status === 'completed' ? 'Concluído' : entry.status === 'error' ? 'Erro' : 'Processando'}</span></td>
                <td>${entry.rows || '-'}</td>
            </tr>
        `;
    }).join('');
}

uploadBtn.onclick = async () => {
    const file = stockFile.files[0];
    const cnpj = stockCnpjInput.value.trim();

    if (!file || !cnpj) {
        showValidationMessage('Selecione um arquivo e informe o CNPJ do fornecedor.', 'error');
        return;
    }

    if (currentFileData && currentFileData.hasErrors) {
        showValidationMessage('Corrija os erros de validação antes de enviar.', 'error');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    const historyEntry = {
        timestamp: new Date().toISOString(),
        fileName: file.name,
        cnpj: cnpj,
        status: 'processing',
        rows: currentFileData?.totalRows || 0
    };

    addToHistory(historyEntry);

    try {
        uploadBtn.disabled = true;
        uploadBtn.innerText = 'Processando...';

        const response = await fetch(`${API_BASE}/estoque/estoque-by-cnpj?cnpj=${encodeURIComponent(cnpj)}`, {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            // Update history status
            uploadHistory[0].status = 'completed';
            localStorage.setItem('ppc_upload_history', JSON.stringify(uploadHistory));
            renderUploadHistory();

            showValidationMessage('✓ Arquivo enviado com sucesso! O fornecedor receberá um aviso quando o processamento terminar.', 'success');

            // Reset form
            stockFile.value = '';
            fileNameLabel.innerText = '';
            filePreview.classList.add('hidden');
            currentFileData = null;
        } else {
            uploadHistory[0].status = 'error';
            localStorage.setItem('ppc_upload_history', JSON.stringify(uploadHistory));
            renderUploadHistory();

            showValidationMessage('Erro no upload. Verifique se o CNPJ está correto.', 'error');
        }
    } catch (err) {
        console.error(err);

        uploadHistory[0].status = 'error';
        localStorage.setItem('ppc_upload_history', JSON.stringify(uploadHistory));
        renderUploadHistory();

        showValidationMessage('Erro de conexão ao tentar subir o estoque.', 'error');
    } finally {
        uploadBtn.disabled = false;
        uploadBtn.innerText = 'Processar Estoque';
    }
};

// Initialize history on load
renderUploadHistory();







// --- Banner Management ---
// Define helper functions first
function showBannerMessage(message, type) {
    const bannerUpdateMessage = document.getElementById('bannerUpdateMessage');
    if (!bannerUpdateMessage) return;

    bannerUpdateMessage.innerHTML = `
        <div class="validation-message ${type}">
            <i class="fas fa-${type === 'error' ? 'exclamation-circle' : type === 'warning' ? 'exclamation-triangle' : 'check-circle'}"></i>
            ${message}
        </div>
    `;

    setTimeout(() => {
        bannerUpdateMessage.innerHTML = '';
    }, 5000);
}

// Define copyToClipboard so it's available when HTML is rendered
window.copyToClipboard = async (text) => {
    console.log('Copying to clipboard:', text);
    try {
        await navigator.clipboard.writeText(text);
        showBannerMessage('URL copiada para a área de transferência!', 'success');
    } catch (err) {
        console.error('Failed to copy:', err);
        showBannerMessage('Erro ao copiar URL', 'error');
    }
};

async function fetchBanners() {
    try {
        setLoader('bannersLoader', true);
        const response = await fetch(`${API_BASE}/banner/all`);

        if (!response.ok) throw new Error('Failed to fetch banners');

        const banners = await response.json();
        console.log('Fetched banners:', banners);
        renderBanners(banners);
    } catch (err) {
        console.error('Error fetching banners:', err);
        if (bannersList) {
            bannersList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Erro ao carregar banners</p>
                </div>
            `;
        }
    } finally {
        setLoader('bannersLoader', false);
    }
}

function renderBanners(banners) {
    console.log('renderBanners called with:', banners);
    console.log('bannersList element:', bannersList);

    if (!bannersList) {
        console.error('bannersList element not found!');
        return;
    }

    if (!banners || banners.length === 0) {
        bannersList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-images"></i>
                <p>Nenhum banner cadastrado ainda</p>
            </div>
        `;
        return;
    }

    const html = banners.map((url, index) => `
        <div class="banner-item">
            <img src="${url}" alt="Banner ${index + 1}" class="banner-preview" onerror="this.src='data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%22800%22 height=%22200%22%3E%3Crect fill=%22%23334155%22 width=%22800%22 height=%22200%22/%3E%3Ctext x=%2250%25%22 y=%2250%25%22 dominant-baseline=%22middle%22 text-anchor=%22middle%22 fill=%22%2394a3b8%22 font-family=%22sans-serif%22 font-size=%2224%22%3EImagem não disponível%3C/text%3E%3C/svg%3E'">
            <div class="banner-info">
                <div class="banner-url">${url}</div>
            </div>
        </div>
    `).join('');

    console.log('Generated HTML length:', html.length);
    bannersList.innerHTML = html;
    console.log('bannersList.innerHTML set');
}

updateBannerBtn.onclick = async () => {
    const cnpj = bannerCnpjInput.value.trim();
    const url = bannerUrlInput.value.trim();

    if (!cnpj || !url) {
        showBannerMessage('Preencha o CNPJ e a URL do banner', 'error');
        return;
    }

    // Basic URL validation
    try {
        new URL(url);
    } catch (e) {
        showBannerMessage('URL inválida. Use o formato: https://exemplo.com/banner.jpg', 'error');
        return;
    }

    try {
        updateBannerBtn.disabled = true;
        updateBannerBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Atualizando...';

        const formData = new URLSearchParams();
        formData.append('novo-banner', url);
        formData.append('cnpj', cnpj);

        const response = await fetch(`${API_BASE}/banner`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        });

        if (response.ok) {
            showBannerMessage('✓ Banner atualizado com sucesso!', 'success');
            bannerCnpjInput.value = '';
            bannerUrlInput.value = '';

            // Refresh banners list
            setTimeout(() => fetchBanners(), 1000);
        } else {
            const errorText = await response.text();
            showBannerMessage(`Erro ao atualizar banner: ${errorText || 'Verifique o CNPJ'}`, 'error');
        }
    } catch (err) {
        console.error(err);
        showBannerMessage('Erro de conexão ao atualizar banner', 'error');
    } finally {
        updateBannerBtn.disabled = false;
        updateBannerBtn.innerHTML = '<i class="fas fa-save"></i> Atualizar Banner';
    }
};

// --- Helpers ---
function setLoader(id, show) {
    const loader = document.getElementById(id);
    if (show) loader.classList.remove('hidden');
    else loader.classList.add('hidden');
}

// Initial
fetchSuppliers();

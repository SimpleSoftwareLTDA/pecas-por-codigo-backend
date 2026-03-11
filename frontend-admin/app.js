// const API_BASE = 'http://localhost:8080/api/v1';
const API_BASE = 'https://backend.pecasporcodigo.com.br/api/v1';

// --- Feature Flags ---
const FLAGS = {
    ENABLE_LOGIN: false
};

console.log('App.js loaded - Script starting');

document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM Content Loaded - Initializing App');

    // Initialize all modules
    initAuth();
    initNav();
    initSuppliers();
    initContactForm();
    initStockUpload();
    initBannerManagement();

    // Initial check
    checkAuth();
});

// --- State Variables (Module Scope) ---
let currentToken = localStorage.getItem('ppc_admin_token');
let allSuppliers = [];
let uploadHistory = JSON.parse(localStorage.getItem('ppc_upload_history') || '[]');
let currentFileData = null;
let editingSupplierId = null;

// --- Auth Module ---
let loginScreen, mainApp, loginTokenInput, loginBtn, loginError, logoutBtn;

function initAuth() {
    loginScreen = document.getElementById('loginScreen');
    mainApp = document.getElementById('mainApp');
    loginTokenInput = document.getElementById('loginToken');
    loginBtn = document.getElementById('loginBtn');
    loginError = document.getElementById('loginError');
    logoutBtn = document.getElementById('logoutBtn');

    if (loginBtn) {
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
    }

    if (logoutBtn) {
        logoutBtn.onclick = () => {
            localStorage.removeItem('ppc_admin_token');
            currentToken = null;
            location.reload();
        };
    }
}

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
    if (loginScreen) loginScreen.classList.remove('hidden');
    if (mainApp) mainApp.classList.add('hidden');
}

function hideLogin() {
    if (loginScreen) loginScreen.classList.add('hidden');
    if (mainApp) mainApp.classList.remove('hidden');
}

// --- Navigation Module ---
function initNav() {
    const navLinks = document.querySelectorAll('.nav-link');
    const tabContents = document.querySelectorAll('.tab-content');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const tabId = link.getAttribute('data-tab');

            navLinks.forEach(l => l.classList.remove('active'));
            tabContents.forEach(tc => tc.classList.remove('active'));

            link.classList.add('active');
            const targetTab = document.getElementById(tabId);
            if (targetTab) targetTab.classList.add('active');

            if (tabId === 'suppliers') fetchSuppliers();
            if (tabId === 'banners') fetchBanners();
        });
    });
}

// --- Suppliers Module ---
let supplierList, supplierForm, supplierModal, addSupplierBtn, supplierSearch;

function initSuppliers() {
    supplierList = document.getElementById('supplierList');
    supplierForm = document.getElementById('supplierForm');
    supplierModal = document.getElementById('supplierModal');
    addSupplierBtn = document.getElementById('addSupplierBtn');
    supplierSearch = document.getElementById('supplierSearch');
    const closeModalBtns = document.querySelectorAll('.close-modal');

    if (addSupplierBtn) {
        addSupplierBtn.onclick = () => {
            editingSupplierId = null;
            if (document.getElementById('modalTitle')) document.getElementById('modalTitle').innerText = 'Novo Fornecedor';
            if (supplierForm) supplierForm.reset();
            if (supplierModal) supplierModal.style.display = 'block';
        };
    }

    closeModalBtns.forEach(btn => btn.onclick = () => {
        if (supplierModal) supplierModal.style.display = 'none';
    });

    if (supplierForm) {
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
    }

    if (supplierSearch) {
        supplierSearch.addEventListener('input', (e) => {
            const term = e.target.value.trim().toLowerCase();
            if (term.length === 0) {
                renderSuppliers(allSuppliers);
                return;
            }

            const filtered = allSuppliers.filter(s =>
                (s.empresa && s.empresa.toLowerCase().includes(term)) ||
                (s.cnpj && s.cnpj.includes(term)) ||
                (s.razaoSocial && s.razaoSocial.toLowerCase().includes(term))
            );
            renderSuppliers(filtered);
        });

        supplierSearch.addEventListener('keypress', async (e) => {
            if (e.key === 'Enter') {
                let cnpj = e.target.value.trim();
                const formattedCnpj = formatCnpj(cnpj);
                if (formattedCnpj.length >= 14) {
                    searchByCnpj(formattedCnpj);
                }
            }
        });
    }
}

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
        if (supplierList) supplierList.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--danger)">Erro ao carregar fornecedores. Verifique se o backend está rodando em :8080</td></tr>`;
    } finally {
        setLoader('tableLoader', false);
    }
}

function renderSuppliers(suppliers) {
    if (!supplierList) return;
    supplierList.innerHTML = '';
    suppliers.forEach(s => {
        const tr = document.createElement('tr');
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

// Helpers for window (global access needed for inline onclick)
window.editSupplier = async (id) => {
    try {
        const response = await fetch(`${API_BASE}/fornecedores/${id}`);
        const s = await response.json();

        editingSupplierId = id;
        if (document.getElementById('modalTitle')) document.getElementById('modalTitle').innerText = 'Editar Fornecedor';

        if (supplierForm) {
            const f = supplierForm.elements;
            if (f['empresa']) f['empresa'].value = s.nome || s.name || '';
            if (f['razaoSocial']) f['razaoSocial'].value = s.razaoSocial || s.socialName || '';
            if (f['cnpj']) f['cnpj'].value = s.cnpj || '';
            if (f['inscricao']) f['inscricao'].value = s.inscricaoEstadual || s.inscricao || '';
            if (f['vendedores']) f['vendedores'].value = s.contato?.vendedores || '';
            if (f['emailPecas']) f['emailPecas'].value = s.contato?.emailPecas || '';
            if (f['whatsappPecas']) f['whatsappPecas'].value = s.contato?.whatsappPecas || '';
            if (f['website']) f['website'].value = s.contato?.website || '';

            const addr = s.endereco || s.address;
            if (f['endereco']) f['endereco'].value = addr?.endereco || addr?.street || '';
            if (f['cidade']) f['cidade'].value = addr?.cidade || addr?.city || '';
            if (f['cep']) f['cep'].value = addr?.cep || '';
            if (f['idEstado']) f['idEstado'].value = addr?.estado?.id || addr?.state?.id || 1;
            if (f['idPlano']) f['idPlano'].value = s.idPlano || 1;
        }

        if (supplierModal) supplierModal.style.display = 'block';
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

async function searchByCnpj(cnpj) {
    try {
        setLoader('tableLoader', true);
        const response = await fetch(`${API_BASE}/fornecedores/cnpj?cnpj=${encodeURIComponent(cnpj)}`);
        if (!response.ok) throw new Error('CNPJ search failed');
        const data = await response.json();
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


// --- Contact Form ---
function initContactForm() {
    const contactForm = document.getElementById('contactForm');
    if (contactForm) {
        contactForm.onsubmit = async (e) => {
            e.preventDefault();
            const payload = {
                name: document.getElementById('contactName').value,
                email: document.getElementById('contactEmail').value,
                subject: document.getElementById('contactSubject').value,
                message: document.getElementById('contactMessage').value
            };
            try {
                const response = await fetch(`${API_BASE}/contact`, {
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
    }
}

// --- Stock Upload ---
let stockFile, stockCnpjInput, uploadBtn, fileNameLabel, filePreview, previewTableHead, previewTableBody, previewStats, validationMessages, uploadHistoryList, downloadTemplateBtn, dropZone;

function initStockUpload() {
    stockFile = document.getElementById('stockFile');
    stockCnpjInput = document.getElementById('stockCnpj');
    uploadBtn = document.getElementById('uploadBtn');
    fileNameLabel = document.getElementById('fileName');
    filePreview = document.getElementById('filePreview');
    previewTableHead = document.getElementById('previewTableHead');
    previewTableBody = document.getElementById('previewTableBody');
    previewStats = document.getElementById('previewStats');
    validationMessages = document.getElementById('validationMessages');
    uploadHistoryList = document.getElementById('uploadHistoryList');
    downloadTemplateBtn = document.getElementById('downloadTemplateBtn');
    dropZone = document.getElementById('dropZone');

    renderUploadHistory();

    if (downloadTemplateBtn) {
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
    }

    if (stockFile) {
        stockFile.onchange = async (e) => {
            if (e.target.files.length > 0) {
                const file = e.target.files[0];
                if (fileNameLabel) fileNameLabel.innerText = file.name;
                await validateAndPreviewFile(file);
            } else {
                if (filePreview) filePreview.classList.add('hidden');
                currentFileData = null;
            }
        };
    }

    if (dropZone) {
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
                if (stockFile) stockFile.files = e.dataTransfer.files;
                if (fileNameLabel) fileNameLabel.innerText = file.name;
                await validateAndPreviewFile(file);
            }
        });
    }

    const verifyBtn = document.getElementById('verifyBtn');
    if (verifyBtn) {
        verifyBtn.onclick = async (e) => {
            e.preventDefault();
            const file = stockFile ? stockFile.files[0] : null;
            if (!file) {
                showValidationMessage('Selecione um arquivo para verificar.', 'error');
                return;
            }
            await validateAndPreviewFile(file);
        };
    }

    if (uploadBtn) {
        uploadBtn.onclick = async (e) => {
            e.preventDefault();
            const file = stockFile ? stockFile.files[0] : null;
            const cnpj = stockCnpjInput ? stockCnpjInput.value.trim() : '';

            if (!file) {
                showValidationMessage('Selecione um arquivo anexado para prosseguir.', 'error');
                return;
            }

            if (!cnpj) {
                if (stockCnpjInput) {
                    stockCnpjInput.setCustomValidity('O CNPJ deve ser preenchido.');
                    stockCnpjInput.reportValidity();
                    stockCnpjInput.focus();

                    const clearValidity = () => {
                        stockCnpjInput.setCustomValidity('');
                        stockCnpjInput.removeEventListener('input', clearValidity);
                    };
                    stockCnpjInput.addEventListener('input', clearValidity);
                } else {
                    showValidationMessage('Informe o CNPJ do fornecedor.', 'error');
                }
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
                    if (stockFile) stockFile.value = '';
                    if (fileNameLabel) fileNameLabel.innerText = '';
                    if (filePreview) filePreview.classList.add('hidden');
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
    }
}

async function validateAndPreviewFile(file) {
    const ext = file.name.split('.').pop().toLowerCase();
    if (ext !== 'tsv' && ext !== 'csv' && ext !== 'txt') {
        showValidationMessage('Apenas arquivos .tsv, .csv ou .txt são permitidos.', 'error');
        return false;
    }

    const verifyBtn = document.getElementById('verifyBtn');
    const uploadBtn = document.getElementById('uploadBtn');
    const originalVerifyHtml = verifyBtn ? verifyBtn.innerHTML : '';

    try {
        if (validationMessages) {
            validationMessages.innerHTML = '<div class="validation-message"><i class="fas fa-spinner fa-spin"></i> Validando arquivo no servidor...</div>';
        }

        // Disable buttons and show loading state on Verify Button
        if (verifyBtn) {
            verifyBtn.disabled = true;
            verifyBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Validando...';
        }
        if (uploadBtn) uploadBtn.disabled = true;

        const formData = new FormData();
        formData.append('file', file);

        const response = await fetch(`${API_BASE}/estoque/validate`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Falha ao validar no servidor');
        }

        const data = await response.json();
        const totalRows = data.totalLines;
        const validCount = data.validLinesCount;
        const invalidCount = data.invalidLinesCount;
        const hasErrors = invalidCount > 0;

        const validationResults = [];

        if (invalidCount > 0) {
            validationResults.push({ type: 'warning', message: `${invalidCount} linha(s) com erros (formato incorreto, código/descrição vazios, etc.) não serão processadas.` });
            validationResults.push({ type: 'success', message: `⚠️ O arquivo contém erros, mas você pode enviá-lo mesmo assim! Serão processadas as ${validCount} linhas corretas e o backend enviará um relatório das rejeitadas.` });
        } else {
            validationResults.push({ type: 'success', message: `✓ Arquivo perfeitamente estruturado! ${validCount} linha(s) de dados prontas para envio.` });
        }

        if (validationMessages) {
            validationMessages.innerHTML = '';
            validationResults.forEach(result => {
                const div = document.createElement('div');
                div.className = `validation-message ${result.type}`;
                div.innerHTML = `<i class="fas fa-${result.type === 'error' ? 'exclamation-circle' : result.type === 'warning' ? 'exclamation-triangle' : 'check-circle'}"></i> <strong>${result.message}</strong>`;
                validationMessages.appendChild(div);
            });
        }

        // --- Frontend Preview Building ---
        if (previewTableHead) {
            previewTableHead.innerHTML = `<tr><th>Código</th><th>Quantidade</th><th>Preço</th><th>Descrição</th></tr>`;
        }
        if (previewTableBody) {
            const previewRows = data.validLines.slice(0, 10);
            previewTableBody.innerHTML = previewRows.map(row => {
                const precoFormatado = (row.priceInCents / 100).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
                return `<tr><td>${row.code}</td><td>${row.quantity}</td><td>${precoFormatado}</td><td>${row.description}</td></tr>`;
            }).join('');
        }

        if (previewStats) previewStats.textContent = `Exibindo ${Math.min(10, validCount)} de ${totalRows} linha(s) analisadas`;

        const invalidLinesSection = document.getElementById('invalidLinesSection');
        const invalidLinesBody = document.getElementById('invalidLinesBody');

        if (invalidCount > 0 && data.invalidLines && data.invalidLines.length > 0) {
            if (invalidLinesSection) invalidLinesSection.classList.remove('hidden');
            if (invalidLinesBody) {
                invalidLinesBody.innerHTML = data.invalidLines.map(line => `<tr><td style="color: var(--danger); font-family: monospace; font-size: 0.9em; white-space: pre-wrap;">${line}</td></tr>`).join('');
            }
        } else {
            if (invalidLinesSection) invalidLinesSection.classList.add('hidden');
        }

        if (filePreview) filePreview.classList.remove('hidden');
        currentFileData = { totalRows, hasErrors };

        return !hasErrors;
    } catch (err) {
        console.error('Error validating file:', err);
        showValidationMessage('Erro ao comunicar com o servidor para validar o arquivo.', 'error');
        return false;
    } finally {
        if (verifyBtn) {
            verifyBtn.disabled = false;
            verifyBtn.innerHTML = originalVerifyHtml;
        }
        if (uploadBtn) uploadBtn.disabled = false;
    }
}

function showValidationMessage(message, type) {
    if (validationMessages) {
        validationMessages.innerHTML = `
            <div class="validation-message ${type}">
                <i class="fas fa-${type === 'error' ? 'exclamation-circle' : 'exclamation-triangle'}"></i>
                ${message}
            </div>
        `;
    }
}

function addToHistory(entry) {
    uploadHistory.unshift(entry);
    if (uploadHistory.length > 20) uploadHistory = uploadHistory.slice(0, 20);
    localStorage.setItem('ppc_upload_history', JSON.stringify(uploadHistory));
    renderUploadHistory();
}

function renderUploadHistory() {
    if (!uploadHistoryList) return;

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


// --- Banner Management Module ---
let bannersList, bannersLoader, bannerCnpjInput, bannerUrlInput, updateBannerBtn, bannerUpdateMessage;

function initBannerManagement() {
    console.log('Initializing Banner Management');
    bannersList = document.getElementById('bannersList');
    bannersLoader = document.getElementById('bannersLoader');
    bannerCnpjInput = document.getElementById('bannerCnpj');
    bannerUrlInput = document.getElementById('bannerUrl');
    updateBannerBtn = document.getElementById('updateBannerBtn');
    bannerUpdateMessage = document.getElementById('bannerUpdateMessage');

    console.log('Banner elements found:', {
        list: !!bannersList,
        loader: !!bannersLoader,
        cnpj: !!bannerCnpjInput,
        url: !!bannerUrlInput,
        btn: !!updateBannerBtn,
        msg: !!bannerUpdateMessage
    });

    if (updateBannerBtn) {
        updateBannerBtn.onclick = async () => {
            console.log('Update banner button clicked');
            let cnpj = bannerCnpjInput.value.trim();
            const url = bannerUrlInput.value.trim();

            console.log('Original CNPJ:', cnpj);
            console.log('Original URL:', url);

            if (!cnpj || !url) {
                showBannerMessage('Preencha o CNPJ e a URL do banner', 'error');
                updateBannerBtn.classList.add('shake');
                setTimeout(() => updateBannerBtn.classList.remove('shake'), 500);
                return;
            }

            // Sanitize CNPJ: format it correctly (standard XX.XXX.XXX/XXXX-XX)
            cnpj = formatCnpj(cnpj);
            const sanitizedUrl = url.trim();
            console.log('Final CNPJ for request:', cnpj);
            console.log('Sanitized URL:', sanitizedUrl);

            // Basic URL validation
            try {
                new URL(sanitizedUrl);
            } catch (e) {
                showBannerMessage('URL inválida. Use o formato: https://exemplo.com/banner.jpg', 'error');
                updateBannerBtn.classList.add('shake');
                setTimeout(() => updateBannerBtn.classList.remove('shake'), 500);
                return;
            }

            try {
                updateBannerBtn.disabled = true;
                updateBannerBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Atualizando...';

                const formData = new URLSearchParams();
                formData.append('novo-banner', sanitizedUrl);
                formData.append('cnpj', cnpj);

                console.log('Sending to backend:', { 'novo-banner': sanitizedUrl, 'cnpj': cnpj });

                const response = await fetch(`${API_BASE}/banner`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData
                });

                if (response.ok) {
                    console.log('Banner update successful');
                    showBannerMessage('✓ Banner atualizado com sucesso!', 'success');
                    bannerCnpjInput.value = '';
                    bannerUrlInput.value = '';

                    // Refresh banners list
                    setTimeout(() => fetchBanners(), 1000);
                } else {
                    const errorText = await response.text();
                    console.error('Banner update failed:', response.status, errorText);
                    showBannerMessage(`Erro ao atualizar banner: ${errorText || 'Verifique o CNPJ'}`, 'error');
                }
            } catch (err) {
                console.error('Error updating banner:', err);
                showBannerMessage('Erro de conexão ao atualizar banner', 'error');
            } finally {
                updateBannerBtn.disabled = false;
                updateBannerBtn.innerHTML = '<i class="fas fa-save"></i> Atualizar Banner';
            }
        };
        console.log('Banner Update Button event listener attached');
    } else {
        console.error('Update Banner Button NOT found');
    }
}

function showBannerMessage(message, type) {
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

// Global copyToClipboard for inline usage
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
    if (!bannersList) return;

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

    bannersList.innerHTML = html;
}

// --- General Helpers ---
function setLoader(id, show) {
    const loader = document.getElementById(id);
    if (!loader) return;
    if (show) loader.classList.remove('hidden');
    else loader.classList.add('hidden');
}

function formatCnpj(cnpj) {
    const digits = cnpj.replace(/\D/g, '');
    if (digits.length !== 14) return digits;
    return `${digits.substring(0, 2)}.${digits.substring(2, 5)}.${digits.substring(5, 8)}/${digits.substring(8, 12)}-${digits.substring(12, 14)}`;
}

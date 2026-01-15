const API_BASE = 'https://backend.pecasporcodigo.com.br/api/v1';

// --- Navigation ---
const navLinks = document.querySelectorAll('.nav-link');
const tabContents = document.querySelectorAll('.tab-content');

const supplierSearch = document.getElementById('supplierSearch');
let allSuppliers = [];

navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const tabId = link.getAttribute('data-tab');

        navLinks.forEach(l => l.classList.remove('active'));
        tabContents.forEach(tc => tc.classList.remove('active'));

        link.classList.add('active');
        document.getElementById(tabId).classList.add('active');

        if (tabId === 'suppliers') fetchSuppliers();
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

stockFile.onchange = (e) => {
    if (e.target.files.length > 0) {
        fileNameLabel.innerText = e.target.files[0].name;
    }
};

uploadBtn.onclick = async () => {
    const file = stockFile.files[0];
    const cnpj = stockCnpjInput.value.trim();

    if (!file || !cnpj) {
        return alert('Selecione um arquivo e informe o CNPJ do fornecedor.');
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
        uploadBtn.disabled = true;
        uploadBtn.innerText = 'Processando...';

        // Updated endpoint: /estoque/estoque-by-cnpj?cnpj={cnpj}
        const response = await fetch(`${API_BASE}/estoque/estoque-by-cnpj?cnpj=${encodeURIComponent(cnpj)}`, {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            alert('Arquivo enviado! O fornecedor receberá um aviso quando o processamento terminar.');
        } else {
            alert('Erro no upload. Verifique se o CNPJ está correto.');
        }
    } catch (err) {
        console.error(err);
        alert('Erro de conexão ao tentar subir o estoque.');
    } finally {
        uploadBtn.disabled = false;
        uploadBtn.innerText = 'Processar Estoque';
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

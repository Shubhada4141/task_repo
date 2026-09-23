// Task Management System - Frontend JavaScript

const API_URL = '/api/tasks';
let currentFilter = 'ALL';
let allTasks = [];

document.addEventListener('DOMContentLoaded', () => {
    // Set default due date to today
    document.getElementById('dueDate').valueAsDate = new Date();
    
    // Attach form submit handler
    document.getElementById('taskForm').addEventListener('submit', handleFormSubmit);

    // Initial load
    loadTasks();
});

function showAlert(message, isError = false) {
    const alertEl = document.getElementById('alertMessage');
    alertEl.textContent = message;
    alertEl.className = isError ? 'alert alert-error' : 'alert alert-success';
    alertEl.style.display = 'block';

    setTimeout(() => {
        alertEl.style.display = 'none';
    }, 4000);
}

// Fetch all tasks from backend
async function loadTasks() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Failed to load tasks from server');
        allTasks = await response.json();
        renderTasks();
    } catch (error) {
        showAlert(error.message, true);
        document.getElementById('taskList').innerHTML = `
            <div class="empty-state">
                <p>⚠️ Unable to connect to server. Ensure Spring Boot is running on port 8081.</p>
            </div>
        `;
    }
}

// Filter tasks by status
function filterTasks(status) {
    currentFilter = status;

    // Update active filter button state
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.getAttribute('data-status') === status) {
            btn.classList.add('active');
        }
    });

    renderTasks();
}

// Render tasks to DOM
function renderTasks() {
    const container = document.getElementById('taskList');
    
    let filtered = allTasks;
    if (currentFilter !== 'ALL') {
        filtered = allTasks.filter(task => task.status === currentFilter);
    }

    if (!filtered || filtered.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>📋 No tasks found for filter: <strong>${currentFilter}</strong></p>
            </div>
        `;
        return;
    }

    container.innerHTML = filtered.map(task => `
        <div class="task-item ${task.status.toLowerCase()}">
            <div class="task-header">
                <span class="task-title">${escapeHtml(task.title)}</span>
                <div class="task-badges">
                    <span class="badge badge-${task.priority.toLowerCase()}">${task.priority}</span>
                    <span class="badge badge-status">${formatStatus(task.status)}</span>
                </div>
            </div>
            
            <div class="task-desc">${escapeHtml(task.description)}</div>

            <div class="task-meta">
                <span>📅 Due: <strong>${task.dueDate}</strong></span>
                <div class="task-actions">
                    ${task.status !== 'COMPLETED' ? `
                        <button class="btn btn-sm btn-success" onclick="completeTask(${task.id})">
                            ✓ Complete
                        </button>
                    ` : ''}
                    <button class="btn btn-sm btn-secondary" onclick="editTask(${task.id})">
                        ✎ Edit
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteTask(${task.id})">
                        🗑 Delete
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// Handle Form Submission (Add or Update)
async function handleFormSubmit(event) {
    event.preventDefault();

    const taskId = document.getElementById('taskId').value;
    const title = document.getElementById('title').value.trim();
    const description = document.getElementById('description').value.trim();
    const dueDate = document.getElementById('dueDate').value;
    const priority = document.getElementById('priority').value;
    const status = document.getElementById('status').value;

    if (!title || !description || !dueDate) {
        showAlert('Please fill in all required fields', true);
        return;
    }

    const payload = { title, description, dueDate, priority, status };
    const isEdit = !!taskId;
    const url = isEdit ? `${API_URL}/${taskId}` : API_URL;
    const method = isEdit ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errData = await response.json();
            throw new Error(errData.message || 'Failed to save task');
        }

        showAlert(isEdit ? 'Task updated successfully!' : 'Task added successfully!');
        resetForm();
        loadTasks();
    } catch (error) {
        showAlert(error.message, true);
    }
}

// Edit Task
function editTask(id) {
    const task = allTasks.find(t => t.id === id);
    if (!task) return;

    document.getElementById('taskId').value = task.id;
    document.getElementById('title').value = task.title;
    document.getElementById('description').value = task.description;
    document.getElementById('dueDate').value = task.dueDate;
    document.getElementById('priority').value = task.priority;
    document.getElementById('status').value = task.status;

    document.getElementById('formTitle').textContent = 'Edit Task #' + task.id;
    document.getElementById('submitBtn').textContent = 'Update Task';
    document.getElementById('cancelBtn').style.display = 'inline-flex';
    
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Complete Task (PATCH)
async function completeTask(id) {
    try {
        const response = await fetch(`${API_URL}/${id}/complete`, {
            method: 'PATCH'
        });

        if (!response.ok) throw new Error('Failed to mark task as completed');

        showAlert('Task marked as completed!');
        loadTasks();
    } catch (error) {
        showAlert(error.message, true);
    }
}

// Delete Task
async function deleteTask(id) {
    if (!confirm('Are you sure you want to delete this task?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to delete task');

        showAlert('Task deleted successfully!');
        loadTasks();
    } catch (error) {
        showAlert(error.message, true);
    }
}

// Cancel Form Edit Mode
function resetForm() {
    document.getElementById('taskId').value = '';
    document.getElementById('taskForm').reset();
    document.getElementById('dueDate').valueAsDate = new Date();
    document.getElementById('formTitle').textContent = 'Add New Task';
    document.getElementById('submitBtn').textContent = 'Add Task';
    document.getElementById('cancelBtn').style.display = 'none';
}

function formatStatus(status) {
    if (status === 'IN_PROGRESS') return 'IN PROGRESS';
    return status;
}

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/&/g, "&amp;")
               .replace(/</g, "&lt;")
               .replace(/>/g, "&gt;")
               .replace(/"/g, "&quot;")
               .replace(/'/g, "&#039;");
}

<script setup>
import { ref, onMounted, computed } from 'vue'

const todos = ref([])
const API_URL = 'http://localhost:8080/api/todos'

const filters = ref({ completed: '', priority: '', sortBy: '' })

const showTaskModal = ref(false)
const showDeleteModal = ref(false)
const isEditing = ref(false)
const isSaving = ref(false)

const taskForm = ref({ id: null, title: '', description: '', completed: false, priority: 'MEDIUM', dueDate: '' })
const taskToDelete = ref(null)

const fetchTodos = async () => {
  const params = new URLSearchParams()
  if (filters.value.completed !== '') params.append('completed', filters.value.completed)
  if (filters.value.priority !== '') params.append('priority', filters.value.priority)
  if (filters.value.sortBy !== '') params.append('sortBy', filters.value.sortBy)

  const response = await fetch(`${API_URL}?${params.toString()}`)
  todos.value = await response.json()
}

const openAddModal = () => {
  taskForm.value = { id: null, title: '', description: '', completed: false, priority: 'MEDIUM', dueDate: '' }
  isEditing.value = false
  showTaskModal.value = true
}

const openEditModal = (todo) => {
  taskForm.value = { ...todo }
  isEditing.value = true
  showTaskModal.value = true
}

const closeTaskModal = () => {
  showTaskModal.value = false
}

const saveTask = async () => {
  if (!taskForm.value.title.trim()) return
  isSaving.value = true

  try {
    let response;

    if (isEditing.value) {
      response = await fetch(`${API_URL}/${taskForm.value.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(taskForm.value)
      })
    } else {
      response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(taskForm.value)
      })
    }

    if (!response.ok) {
      const errorData = await response.json()
      alert("Save failed. Please check that the due date is not in the past.")
      return 
    }

    await fetchTodos()

    closeTaskModal() 
  } catch (error) {
    console.error("Error saving task:", error)
  } finally {
    isSaving.value = false
  }
}

const confirmDelete = (todo) => {
  taskToDelete.value = todo
  showDeleteModal.value = true
}

const executeDelete = async () => {
  if (!taskToDelete.value) return
  await fetch(`${API_URL}/${taskToDelete.value.id}`, { method: 'DELETE' })
  todos.value = todos.value.filter(t => t.id !== taskToDelete.value.id)
  showDeleteModal.value = false
  taskToDelete.value = null
}

const toggleComplete = async (todo) => {
  const updatedTodo = { ...todo, completed: !todo.completed }
  await fetch(`${API_URL}/${todo.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updatedTodo)
  })
  await fetchTodos()
  todo.completed = !todo.completed
}

const clearCompleted = async () => {
  await fetch(`${API_URL}/completed`, { method: 'DELETE' })
  todos.value = todos.value.filter(t => !t.completed)
}


const sortedTodos = computed(() => {
  return [...todos.value].sort((a, b) => {
    if (a.completed !== b.completed) return a.completed ? 1 : -1
    return 0 
  })
})

const totalTasks = computed(() => todos.value.length)
const completedTasks = computed(() => todos.value.filter(t => t.completed).length)

onMounted(() => {
  fetchTodos()
})
</script>

<template>
  <div class="page-wrapper">
    <div class="app-container">
      <header class="app-header">
        <div class="header-titles">
          <h1>My Tasks</h1>
          <div class="header-stats">
            <p class="task-counter" v-if="totalTasks > 0">{{ totalTasks }} tasks · {{ completedTasks }} completed</p>
            <p class="task-counter" v-else>0 tasks</p>
            <button v-if="completedTasks > 0" class="clear-btn" @click="clearCompleted">Clear completed</button>
          </div>
        </div>
        <button class="add-button" @click="openAddModal">+ Add Task</button>
      </header>

      <div class="filters-bar">
        <select v-model="filters.completed" @change="fetchTodos" class="filter-select">
          <option value="">All Statuses</option>
          <option value="false">Active Only</option>
          <option value="true">Completed Only</option>
        </select>
        
        <select v-model="filters.priority" @change="fetchTodos" class="filter-select">
          <option value="">All Priorities</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
        </select>
        
        <select v-model="filters.sortBy" @change="fetchTodos" class="filter-select">
          <option value="">Sort: Default</option>
          <option value="dueDate">Sort: Due Date</option>
          <option value="priority">Sort: Priority</option>
          <option value="priority-date">Sort: Priority & Date</option>
        </select>
      </div>

      <div v-if="todos.length === 0" class="empty-state">
        <h3>No tasks yet</h3>
        <p>Create your first task or change your filters.</p>
      </div>

      <ul v-else class="todo-list">
        <li v-for="todo in sortedTodos" :key="todo.id" :class="{ completed: todo.completed }">
          
          <div class="task-content">
            <input type="checkbox" :checked="todo.completed" @change="toggleComplete(todo)" />
            <div class="task-text">
              <span class="task-title">{{ todo.title }}</span>
              <span class="task-desc" v-if="todo.description">{{ todo.description }}</span>
              
              <div class="task-meta" v-if="todo.priority || todo.dueDate">
                <span v-if="todo.priority" :class="['badge', 'badge-' + todo.priority.toLowerCase()]">{{ todo.priority }}</span>
                <span v-if="todo.dueDate" class="due-date">📅 {{ todo.dueDate }}</span>
              </div>
            </div>
          </div>

          <div class="task-actions">
            <button class="icon-btn edit-btn" @click="openEditModal(todo)">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
            </button>
            <button class="icon-btn delete-btn" @click="confirmDelete(todo)">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
            </button>
          </div>

        </li>
      </ul>
      
    </div>

    <div v-if="showTaskModal" class="modal-overlay" @click.self="closeTaskModal">
      <div class="modal-card">
        <div class="modal-header">
          <h2>{{ isEditing ? 'Edit Task' : 'Add Task' }}</h2>
          <button class="close-btn" @click="closeTaskModal">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
        
        <div class="modal-body">
          <label>Title</label>
          <input type="text" v-model="taskForm.title" placeholder="Enter task title..." @keyup.enter="saveTask" />
          
          <label>Description</label>
          <textarea v-model="taskForm.description" placeholder="Enter task description..." rows="3"></textarea>
        
          <div class="form-row">
            <div class="form-group">
              <label>Priority</label>
              <select v-model="taskForm.priority">
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>
            <div class="form-group">
              <label>Due Date</label>
              <input type="date" v-model="taskForm.dueDate" />
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeTaskModal">Cancel</button>
          <button class="btn-primary" @click="saveTask" :disabled="isSaving || !taskForm.title.trim()">
            {{ isEditing ? 'Save Changes' : 'Add Task' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showDeleteModal" class="modal-overlay" @click.self="showDeleteModal = false">
      <div class="modal-card">
        <h2>Delete task?</h2>
        <p>Are you sure you want to delete <br><strong>"{{ taskToDelete?.title }}"</strong>?</p>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showDeleteModal = false">Cancel</button>
          <button class="btn-danger" @click="executeDelete">Delete</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
body { margin: 0; background-color: #F7F8FA; }
</style>

<style scoped>
.page-wrapper {
  min-height: 100vh;
  padding: 40px 20px;
  font-family: Inter, SF Pro, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  color: #111827;
}

.app-container {
  max-width: 800px; 
  margin: 0 auto;
  background-color: #FFFFFF;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.app-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  padding: 24px 32px;
  border-bottom: 1px solid #E5E7EB; 
}
.header-titles h1 { 
  font-size: 22px; 
  margin: 0 0 6px 0; 
  font-weight: 700;
}
.header-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}
.task-counter {
  margin: 0;
  font-size: 13px;
  color: #6B7280;
}
.clear-btn {
  background: none;
  border: none;
  color: #EF4444;
  font-size: 13px;
  cursor: pointer;
  padding: 0;
}
.clear-btn:hover { text-decoration: underline; }

.add-button { 
  background-color: #3B82F6; 
  color: white; 
  border: none; 
  border-radius: 6px; 
  padding: 8px 16px; 
  cursor: pointer; 
  font-weight: 500;
  font-size: 14px;
}
.add-button:hover { background-color: #2563EB; }

.filters-bar {
  display: flex;
  gap: 12px;
  padding: 12px 32px;
  background-color: #F9FAFB;
  border-bottom: 1px solid #E5E7EB;
}
.filter-select {
  padding: 6px 12px;
  border: 1px solid #D1D5DB;
  border-radius: 6px;
  font-size: 13px;
  color: #374151;
  background-color: #FFFFFF;
  outline: none;
  cursor: pointer;
}
.filter-select:focus { border-color: #3B82F6; }

.empty-state { padding: 32px; text-align: left; }
.empty-state h3 { color: #111827; font-size: 16px; font-weight: 600; margin: 0 0 8px 0; }
.empty-state p { margin: 0; font-size: 14px; color: #6B7280; }

.todo-list { list-style-type: none; padding: 0; margin: 0; }
.todo-list li { 
  display: flex; 
  justify-content: space-between; 
  align-items: flex-start;
  padding: 20px 32px;
  border-bottom: 1px solid #F3F4F6; 
}
.todo-list li:last-child { border-bottom: none; }

.task-content { display: flex; align-items: flex-start; gap: 16px; }
input[type="checkbox"] { width: 18px; height: 18px; margin-top: 2px; cursor: pointer; accent-color: #3B82F6; }
.task-text { display: flex; flex-direction: column; }
.task-title { font-size: 15px; font-weight: 500; color: #111827;}
.task-desc { font-size: 13px; color: #6B7280; margin-top: 4px; line-height: 1.4;}

.task-meta { display: flex; gap: 8px; margin-top: 8px; align-items: center; }
.badge { font-size: 11px; padding: 2px 6px; border-radius: 4px; font-weight: 600; }
.badge-high { background-color: #FEE2E2; color: #991B1B; }
.badge-medium { background-color: #FEF3C7; color: #92400E; }
.badge-low { background-color: #E0F2FE; color: #075985; }
.due-date { font-size: 12px; color: #6B7280; }

.task-actions { display: flex; gap: 12px; margin-top: 2px;}
.icon-btn { background: none; border: none; cursor: pointer; color: #9CA3AF; padding: 4px; display: flex; align-items: center; }
.delete-btn:hover { color: #EF4444; }
.edit-btn:hover { color: #3B82F6; }

.completed .task-title { text-decoration: line-through; color: #9CA3AF; }
.completed .task-desc { color: #9CA3AF; }

.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(17, 24, 39, 0.4);
  display: flex; justify-content: center; align-items: center; z-index: 1000;
}
.modal-card { 
  background-color: #FFFFFF; padding: 24px; border-radius: 8px; 
  width: 100%; max-width: 440px; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1); 
}
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.modal-header h2 { margin: 0; font-size: 18px; font-weight: 600;}
.close-btn { background: none; border: none; cursor: pointer; color: #6B7280; display: flex; padding: 0;}
.close-btn:hover { color: #111827; }

.modal-body { display: flex; flex-direction: column; gap: 8px; margin-bottom: 34px; }
.modal-body label { font-size: 13px; font-weight: 500; color: #374151; margin-top: 8px;}
.modal-body input, .modal-body textarea, .modal-body select {
  padding: 10px 12px; border: 1px solid #D1D5DB; border-radius: 6px;
  font-size: 14px; font-family: inherit; color: #111827; box-sizing: border-box; width: 100%;
}
.modal-body input:focus, .modal-body textarea:focus, .modal-body select:focus {
  outline: none; border-color: #3B82F6; box-shadow: 0 0 0 1px #3B82F6;
}

.form-row { display: flex; gap: 16px; width: 100%; }
.form-group { flex: 1; }

.modal-footer { display: flex; justify-content: flex-end; gap: 12px; }
.btn-cancel { background: #FFFFFF; border: 1px solid #D1D5DB; color: #374151; font-weight: 500; cursor: pointer; padding: 8px 16px; border-radius: 6px; font-size: 14px; }
.btn-cancel:hover { background: #F9FAFB; }
.btn-primary { background-color: #3B82F6; color: white; border: none; border-radius: 6px; padding: 8px 16px; cursor: pointer; font-weight: 500; font-size: 14px;}
.btn-primary:hover:not(:disabled) { background-color: #2563EB; }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-danger { background-color: #EF4444; color: white; border: none; border-radius: 6px; padding: 8px 16px; cursor: pointer; font-weight: 500; font-size: 14px;}
.btn-danger:hover { background-color: #DC2626; }
</style>
import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './index.css'

console.log('VITE_API_URL =', import.meta.env.VITE_API_URL)

createApp(App).use(router).mount('#app')

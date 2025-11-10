import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // ← auto: dev=localhost, prod=Render
  headers: { 'Content-Type': 'application/json' },
})

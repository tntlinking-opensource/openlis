import axios from 'axios'

export function fetchTubeColors(keyword) {
  return axios.get('/api/tube-color/list', { params: { keyword } })
}

export function saveTubeColor(data) {
  return axios.post('/api/tube-color/save', data)
}

export function deleteTubeColor(pym) {
  return axios.delete('/api/tube-color/', { params: { pym } })
}
import axios from 'axios'

export function fetchSpecimenTypes(params) {
  return axios.get('/api/specimen-type/list', { params })
}

export function saveSpecimenType(data) {
  return axios.post('/api/specimen-type/save', data)
}

export function deleteSpecimenType(bm) {
  return axios.delete(`/api/specimen-type/${bm}`)
}

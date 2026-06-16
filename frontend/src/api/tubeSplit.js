import axios from 'axios'

export function fetchTubeCategories() {
  return axios.get('/api/tube-split/categories')
}

export function saveTubeCategory(data) {
  return axios.post('/api/tube-split/category/save', data)
}

export function fetchTubeSubcategories(params) {
  return axios.get('/api/tube-split/subcategories', { params })
}

export function saveTubeSubcategory(data) {
  return axios.post('/api/tube-split/subcategory/save', data)
}

export function fetchComboItemsByCat(xlbh) {
  return axios.get('/api/tube-split/combo-items', { params: { xlbh } })
}

export function fetchAvailableComboItems(xlbh) {
  return axios.get('/api/tube-split/available-combo-items', { params: { xlbh } })
}

export function saveComboMapping(xlbh, items) {
  return axios.post('/api/tube-split/combo-mapping/save', { xlbh, items })
}

export function removeComboMapping(xlbh, zhid) {
  return axios.delete('/api/tube-split/combo-mapping', { params: { xlbh, zhid } })
}
import axios from 'axios'

export function fetchFeeItems(params) {
  return axios.get('/api/material-fee/fee-items', { params })
}

export function bindMaterialFee(data) {
  return axios.post('/api/material-fee/bind', data)
}

export function unbindMaterialFee(data) {
  return axios.post('/api/material-fee/unbind', data)
}

export function syncMaterialFee(data) {
  return axios.post('/api/material-fee/sync', data)
}

export function fetchMaterialBindings(params) {
  return axios.get('/api/material-fee/bindings', { params })
}

export function fetchTubeCategories() {
  return axios.get('/api/tube-split/categories')
}

export function fetchTubeSubcategories(params) {
  return axios.get('/api/tube-split/subcategories', { params })
}
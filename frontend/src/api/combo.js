import axios from 'axios'

export function fetchCombos(params) {
  return axios.get('/api/combo/list', { params })
}

export function searchCombos(name) {
  return axios.get('/api/combo/search', { params: { name } })
}

export function saveCombo(data) {
  return axios.post('/api/combo/save', data)
}

export function deleteCombo(zhid) {
  return axios.delete(`/api/combo/${zhid}`)
}

export function fetchComboItems(zhid) {
  return axios.get(`/api/combo/${zhid}/items`)
}

export function addComboItem(zhid, data) {
  return axios.post(`/api/combo/${zhid}/add-item`, data)
}

export function removeComboItem(zhid, xmid) {
  return axios.delete(`/api/combo/${zhid}/remove-item/${xmid}`)
}

export function reorderComboItems(zhid, xmidOrder) {
  return axios.put(`/api/combo/${zhid}/reorder`, xmidOrder)
}

export function copyComboFrom(zhid, sourceId) {
  return axios.post(`/api/combo/${zhid}/copy-from/${sourceId}`)
}

export function fetchCompletionSettings(params) {
  return axios.get('/api/combo/completion-settings', { params })
}

export function saveCompletionSetting(data) {
  return axios.post('/api/combo/completion-settings/save', data)
}

export function deleteCompletionSetting(id) {
  return axios.delete(`/api/combo/completion-settings/${id}`)
}

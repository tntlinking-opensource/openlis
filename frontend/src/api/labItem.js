import axios from 'axios'

export function fetchTestItems(params) {
  return axios.get('/api/test-item/list', { params })
}

export function searchTestItems(pym) {
  return axios.get('/api/test-item/search', { params: { pym } })
}

export function saveTestItem(data) {
  return axios.post('/api/test-item/save', data)
}

export function deleteTestItem(xmid) {
  return axios.delete(`/api/test-item/${xmid}`)
}

export function fetchTestItemTypes() {
  return axios.get('/api/test-item/types')
}

export function fetchPrecisions() {
  return axios.get('/api/test-item/precisions')
}

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

export function fetchInstrumentComboTree() {
  return axios.get('/api/instrument-combo/tree')
}

export function assignCombo(data) {
  return axios.post('/api/instrument-combo/assign', data)
}

export function removeInstrumentCombo(instId, comboId) {
  return axios.delete(`/api/instrument-combo/${instId}/${comboId}`)
}

export function fetchUnassignedCombos(sbDjid) {
  return axios.get('/api/instrument-combo/unassigned-combos', { params: { sbDjid } })
}

export function fetchInstrumentItems(sbDjid) {
  return axios.get(`/api/instrument-item/${sbDjid}/items`)
}

export function addInstrumentItem(data) {
  return axios.post('/api/instrument-item/add-item', data)
}

export function removeInstrumentItem(sbDjid, xmid) {
  return axios.delete(`/api/instrument-item/${sbDjid}/${xmid}`)
}

export function saveInstrumentItem(data) {
  return axios.post('/api/instrument-item/inst-item', data)
}

export function fetchInstrumentItemTree() {
  return axios.get('/api/instrument-item/tree')
}

export function fetchRefRanges(instId, itemId) {
  return axios.get(`/api/instrument-item/${instId}/${itemId}/ref-range`)
}

export function saveRefRange(data) {
  return axios.post('/api/instrument-item/ref-range', data)
}

export function deleteRefRange(id) {
  return axios.delete(`/api/instrument-item/ref-range/${id}`)
}

export function fetchDefault(instId, itemId) {
  return axios.get(`/api/instrument-item/${instId}/${itemId}/default`)
}

export function saveDefault(data) {
  return axios.post('/api/instrument-item/default', data)
}

export function batchSaveCoeff(data) {
  return axios.post('/api/instrument-item/batch-coeff', data)
}

export function fetchDataReplaceSettings(instId, itemId) {
  return axios.get(`/api/instrument-item/${instId}/${itemId}/data-replace`)
}

export function saveDataReplaceSetting(data) {
  return axios.post('/api/instrument-item/data-replace', data)
}

export function deleteDataReplaceSetting(id) {
  return axios.delete(`/api/instrument-item/data-replace/${id}`)
}

export function fetchFormula(instId, itemId) {
  return axios.get(`/api/instrument-item/${instId}/${itemId}/formula`)
}

export function saveFormula(data) {
  return axios.post('/api/instrument-item/formula', data)
}

export function fetchFormulaList(sbDjid) {
  return axios.get('/api/instrument-item/formula/list', { params: { sbDjid } })
}

export function fetchProjectListByInstrument(sbDjid) {
  return axios.get('/api/instrument-item/projects', { params: { sbDjid } })
}

export function searchProjectsApi(pym, sbDjid) {
  return axios.get('/api/test-item/search-by-pym', { params: { pym, sbDjid } })
}

export function fetchTatSettings(params) {
  return axios.get('/api/tat/settings', { params })
}

export function saveTatSetting(data) {
  return axios.post('/api/tat/settings/save', data)
}

export function deleteTatSetting(sbDjid, brlb, syqk, zhid) {
  return axios.delete(`/api/tat/settings/${sbDjid}/${brlb}/${syqk}/${zhid}`)
}

export function autoCalculateTat() {
  return axios.get('/api/tat/settings/auto-calculate')
}

export function fetchTatStatistics(params) {
  return axios.get('/api/tat/statistics', { params })
}

export function fetchTatOvertime(params) {
  return axios.get('/api/tat/overtime', { params })
}

export function fetchTatTrend(params) {
  return axios.get('/api/tat/trend', { params })
}
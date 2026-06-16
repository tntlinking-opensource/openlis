import axios from 'axios'

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

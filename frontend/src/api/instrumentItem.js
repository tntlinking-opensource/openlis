import axios from 'axios'

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

export function fetchInstrumentCoefficients(sbDjid) {
  return axios.get(`/api/instrument-item/${sbDjid}/coefficients`)
}

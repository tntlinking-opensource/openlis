import axios from 'axios'

export function fetchBillingSamples(params) {
  return axios.get('/api/billing/samples', { params })
}

export function fetchBillingDetails(brxxId) {
  return axios.get(`/api/billing/details/${brxxId}`)
}

export function fetchBillingStatus(sampleId) {
  return axios.get(`/api/billing/status/${sampleId}`)
}

export function confirmBilling(sampleId, data) {
  return axios.post(`/api/billing/confirm/${sampleId}`, data)
}

export function cancelBilling(sampleId, data) {
  return axios.post(`/api/billing/cancel/${sampleId}`, data)
}

export function batchConfirmBilling(brxxIds, data) {
  return axios.post('/api/billing/batch-confirm', { brxxIds, ...data })
}

export function batchCancelBilling(brxxIds, data) {
  return axios.post('/api/billing/batch-cancel', { brxxIds, ...data })
}

export function batchInvalidateBilling(brxxIds, data) {
  return axios.post('/api/billing/batch-invalidate', { brxxIds, ...data })
}
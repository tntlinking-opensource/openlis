import axios from 'axios'

export function generateBarcode() {
  return axios.post('/api/barcode/generate')
}

export function printBarcodeLabels(brxxIds) {
  return axios.post('/api/barcode/print-label', { brxxIds })
}

export function reprintBarcodeLabels(brxxIds) {
  return axios.post('/api/barcode/reprint', { brxxIds })
}

export function fetchBarcodeConfig() {
  return axios.get('/api/barcode/config')
}

export function fetchUnprintedSamples(params) {
  return axios.get('/api/barcode/unprinted-samples', { params })
}

export function printLabelPdf(brxxId) {
  return axios.get(`/api/barcode/print-pdf/${brxxId}`, { responseType: 'blob' })
}

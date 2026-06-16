import axios from 'axios'

export function fetchAstmStatus(params) {
  return axios.get('/api/protocol/astm/status', { params })
}

export function startAstm(data) {
  return axios.post('/api/protocol/astm/start', data)
}

export function stopAstm(data) {
  return axios.post('/api/protocol/astm/stop', data)
}

export function fetchAstmLog(params) {
  return axios.get('/api/protocol/astm/log', { params })
}

export function parseAstm(data) {
  return axios.post('/api/protocol/astm/parse', data)
}

export function sendHl7(data) {
  return axios.post('/api/protocol/hl7/send', data)
}

export function parseHl7(data) {
  return axios.post('/api/protocol/hl7/parse', data)
}

export function fetchHl7Messages(params) {
  return axios.get('/api/protocol/hl7/messages', { params })
}

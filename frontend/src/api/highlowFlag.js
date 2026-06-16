import axios from 'axios'

export function fetchHighLowFlags() {
  return axios.get('/api/highlow-flag/list')
}

export function fetchActiveFlags() {
  return axios.get('/api/highlow-flag/active')
}

export function saveHighLowFlags(data) {
  return axios.post('/api/highlow-flag/save', data)
}

export function deleteHighLowFlags(bhid) {
  return axios.delete(`/api/highlow-flag/${bhid}`)
}

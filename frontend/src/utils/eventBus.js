import mitt from 'mitt'

const emitter = mitt()

export const openBatchPrintDialog = (selectedIds, date) => {
  emitter.emit('open-batch-print', { selectedIds, date })
}

export const onBatchPrintDialog = (handler) => {
  emitter.on('open-batch-print', handler)
}

export default emitter

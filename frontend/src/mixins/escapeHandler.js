import { onMounted, onUnmounted } from 'vue'

let escHandler = null

export function useEscapeHandler(options = {}) {
  const {
    onEscape = null,
    clearState = null,
    closeDialog = null,
    enabled = true
  } = options

  const handleKeyDown = (event) => {
    if (event.key === 'Escape' && enabled) {
      if (closeDialog) {
        closeDialog()
      } else if (onEscape) {
        onEscape()
      } else if (clearState) {
        clearState()
      }
    }
  }

  onMounted(() => {
    document.addEventListener('keydown', handleKeyDown)
  })

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyDown)
  })

  return {
    handleKeyDown
  }
}

export function setupGlobalEscapeHandler(options = {}) {
  const {
    onEscape = null,
    clearState = null,
    enabled = true
  } = options

  if (escHandler) {
    document.removeEventListener('keydown', escHandler)
  }

  escHandler = (event) => {
    if (event.key === 'Escape' && enabled) {
      if (onEscape) {
        onEscape()
      } else if (clearState) {
        clearState()
      }
    }
  }

  document.addEventListener('keydown', escHandler)
}

export function removeGlobalEscapeHandler() {
  if (escHandler) {
    document.removeEventListener('keydown', escHandler)
    escHandler = null
  }
}

export default {
  useEscapeHandler,
  setupGlobalEscapeHandler,
  removeGlobalEscapeHandler
}

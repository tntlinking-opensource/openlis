import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)

let popperFixRAF = null
function fixMenuPoppers() {
  const poppers = document.querySelectorAll('.el-popper')
  poppers.forEach(popper => {
    if (popper.style.display === 'none') return
    const menu = popper.querySelector('.el-menu--horizontal')
    if (!menu) return

    const trigger = document.querySelector('.el-sub-menu[aria-expanded="true"] > .el-sub-menu__title')
    if (trigger) {
      const triggerRect = trigger.getBoundingClientRect()
      const style = popper.style
      const currentLeft = parseFloat(style.left) || 0
      const currentTop = parseFloat(style.top) || 0

      if (Math.abs(currentLeft - triggerRect.left) > 2) {
        style.left = triggerRect.left + 'px'
      }
      if (Math.abs(currentTop - 80) > 2) {
        style.top = '80px'
      }
    }
  })
}

function schedulePopperFix() {
  popperFixRAF = requestAnimationFrame(() => {
    fixMenuPoppers()
    schedulePopperFix()
  })
}

app.mount('#app')
schedulePopperFix()


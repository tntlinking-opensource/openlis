import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import MainFrame from '../views/MainFrame.vue'
import SampleManagement from '../views/SampleManagement.vue'
import QcManagement from '../views/QcManagement.vue'
import QueryStatistics from '../views/QueryStatistics.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/main',
    name: 'MainFrame',
    component: MainFrame,
    children: [
      {
        path: 'sample',
        name: 'SampleManagement',
        component: SampleManagement
      },
      {
        path: 'qc',
        name: 'QcManagement',
        component: QcManagement
      },
      {
        path: 'query',
        name: 'QueryStatistics',
        component: QueryStatistics
      }
    ]
  },
  // 兼容旧路由：/sample 重定向到 /main/sample
  {
    path: '/sample',
    redirect: '/main/sample'
  },
  {
    path: '/qc',
    redirect: '/main/qc'
  },
  {
    path: '/query',
    redirect: '/main/query'
  },
  // 注意：6.1-6.5等功能现在通过对话框实现，不再使用独立路由
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router


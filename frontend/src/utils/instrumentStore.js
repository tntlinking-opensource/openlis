import { reactive } from 'vue'
import axios from 'axios'

const state = reactive({
  loaded: false,
  sbDjid: null,
  sbmc: '',
  sbbm: '',
  ksdm: '',
  gzzdm: '',
  pym: '',
  zxbz: true,
  tybz: false,
  comsm: '',
  btl: 9600,
  jyw: '无',
  sjw: 8,
  tzw: 1,
  xmxsfs: '',
  bgbt: '',
  bgyj: '',
  bgjglx: null,
  mrzhid: null,
  tx: '无',
  dyfs: '不打印',
  shzfs: '不审核',
  sxpl: 0,
  ycxwc: false,
  xsfs: '按仪器',
  bblb: '',
  bgbh: '',
  bgmc: '',
  xslb: '通用',
  zklb: '结果',
  yqzd: '',
  zjjgts: 7,
  zkjh: '',
  jzjh: '',
  cjcx: '',
  szdm: '',
  kztsbz: false,
  jkxmxz: false,
  fsztsbz: false,
  zerotsbz: false,
  ip: '',
  dk: '',
  sjklj: '',
  wjdz: '',
  bfdz: '',
  wjyhm: '',
  wjmm: '',
  yszcz: '#000000',
  yspgz: '#FF0000',
  yspdz: '#0000FF',
  ysbjgz: '#FF00FF',
  ysbjdz: '#008000',
  yswsh: '#000000',
  ysysh: '#000000',
  ysycy: '#000000',
  ysydy: '#000000',
  ysyjy: '#000000',
  ysycz: '#FF0000',
  yswjz: '#FF0000',
  ysjgwc: '#000000',
})

export function useInstrumentStore() {
  const loadFromDevice = async (device) => {
    const sbDjid = device.sb_djid || device.sbDjid
    if (!sbDjid) return
    try {
      const res = await axios.get(`/api/basic/instrument/${sbDjid}`)
      const d = res.data || {}
      state.loaded = true
      state.sbDjid = d.sb_djid
      state.sbmc = d.sbmc || ''
      state.sbbm = d.sbbm || ''
      state.ksdm = d.ksdm || ''
      state.gzzdm = d.gzzdm || ''
      state.pym = d.pym || ''
      state.zxbz = d.zxbz ?? true
      state.tybz = d.tybz ?? false
      state.comsm = d.comsm || ''
      state.btl = d.btl || 9600
      state.jyw = d.jyw || '无'
      state.sjw = d.sjw || 8
      state.tzw = d.tzw || 1
      state.xmxsfs = d.xmxsfs || ''
      state.bgbt = d.bgbt || ''
      state.bgyj = d.bgyj || ''
      state.bgjglx = d.bgjglx || null
      state.mrzhid = d.mrzhid || null
      state.tx = d.tx || '无'
      state.dyfs = d.dyfs || '不打印'
      state.shzfs = d.shzfs || '不审核'
      state.sxpl = d.sxpl || 0
      state.ycxwc = d.ycxwc ?? false
      state.xsfs = d.xsfs || '按仪器'
      state.bblb = d.bblb || ''
      state.bgbh = d.bgbh || ''
      state.bgmc = d.bgmc || ''
      state.xslb = d.xslb || '通用'
      state.zklb = d.zklb || '结果'
      state.yqzd = d.yqzd || ''
      state.zjjgts = d.zjjgts || 7
      state.zkjh = d.zkjh || ''
      state.jzjh = d.jzjh || ''
      state.cjcx = d.cjcx || ''
      state.szdm = d.szdm || ''
      state.kztsbz = d.kztsbz ?? false
      state.jkxmxz = d.jkxmxz ?? false
      state.fsztsbz = d.fsztsbz ?? false
      state.zerotsbz = d.zerotsbz ?? false
      state.ip = d.ip || ''
      state.dk = d.dk || ''
      state.sjklj = d.sjklj || ''
      state.wjdz = d.wjdz || ''
      state.bfdz = d.bfdz || ''
      state.wjyhm = d.wjyhm || ''
      state.wjmm = d.wjmm || ''
      state.yszcz = d.yszcz || '#000000'
      state.yspgz = d.yspgz || '#FF0000'
      state.yspdz = d.yspdz || '#0000FF'
      state.ysbjgz = d.ysbjgz || '#FF00FF'
      state.ysbjdz = d.ysbjdz || '#008000'
      state.yswsh = d.yswsh || '#000000'
      state.ysysh = d.ysysh || '#000000'
      state.ysycy = d.ysycy || '#000000'
      state.ysydy = d.ysydy || '#000000'
      state.ysyjy = d.ysyjy || '#000000'
      state.ysycz = d.ysycz || '#FF0000'
      state.yswjz = d.yswjz || '#FF0000'
      state.ysjgwc = d.ysjgwc || '#000000'
    } catch (e) {
      console.error('加载仪器配置失败:', e)
    }
  }

  const clear = () => {
    state.loaded = false
    state.sbDjid = null
    state.sbmc = ''
  }

  return { state, loadFromDevice, clear }
}

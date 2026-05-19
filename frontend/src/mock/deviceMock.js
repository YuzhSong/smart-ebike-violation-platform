// 设备管理 Mock 数据：后续可由真实后端设备表替换
export const deviceListMock = [
  {
    id: 'CAM-BJTU-001',
    location: '学院南路天桥',
    type: '边缘摄像头',
    onlineStatus: '在线',
    lastUploadTime: '2026-05-18 14:22:35',
    todayTasks: 486,
    maintenanceStatus: '正常',
  },
  {
    id: 'CAM-BJTU-002',
    location: '北京交通大学东门',
    type: '边缘摄像头',
    onlineStatus: '离线',
    lastUploadTime: '2026-05-18 09:07:11',
    todayTasks: 112,
    maintenanceStatus: '维护中',
  },
  {
    id: 'EDGE-001',
    location: '路侧边缘计算节点',
    type: 'Jetson Nano',
    onlineStatus: '在线',
    lastUploadTime: '2026-05-18 14:24:02',
    todayTasks: 735,
    maintenanceStatus: '正常',
  },
  {
    id: 'CAM-BJTU-003',
    location: '主校区南门',
    type: '高点抓拍摄像机',
    onlineStatus: '在线',
    lastUploadTime: '2026-05-18 14:20:47',
    todayTasks: 521,
    maintenanceStatus: '正常',
  },
];

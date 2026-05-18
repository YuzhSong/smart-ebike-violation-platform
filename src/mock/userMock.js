export const currentUserMock = {
  id: 'U-BJTU-1001',
  name: '张明',
  role: 'user',
  region: '北京市海淀区',
};

// 用户管理 Mock 数据：管理员端统一从 api 层读取
export const userListMock = [
  {
    id: 'U-BJTU-1001',
    name: '张明',
    phone: '138****5521',
    vehicleNo: 'E-BJ-20031',
    violationCount: 3,
    latestViolationTime: '2026-05-18 08:22:15',
    status: '正常',
  },
  {
    id: 'U-BJTU-1002',
    name: '李华',
    phone: '136****3308',
    vehicleNo: 'E-BJ-12875',
    violationCount: 1,
    latestViolationTime: '2026-05-17 18:10:05',
    status: '冻结',
  },
  {
    id: 'U-BJTU-1003',
    name: '王敏',
    phone: '139****7741',
    vehicleNo: 'E-BJ-33710',
    violationCount: 0,
    latestViolationTime: '-',
    status: '正常',
  },
];

export const adminDashboardStatsMock = {
  todayViolations: 1286,
  pendingReview: 193,
  deviceOnlineRate: 94.7,
  processedCount: 1147,
  typeRanking: [
    { name: '未佩戴头盔', count: 428 },
    { name: '逆向行驶', count: 316 },
    { name: '闯红灯', count: 291 },
    { name: '违规载人', count: 251 },
  ],
  locationRanking: [
    { name: '学院南路天桥', count: 176 },
    { name: '北京交通大学东门', count: 152 },
    { name: '主校区南门', count: 143 },
    { name: '西直门桥附近', count: 132 },
  ],
  trend: [
    { day: '05-12', count: 151 },
    { day: '05-13', count: 169 },
    { day: '05-14', count: 162 },
    { day: '05-15', count: 178 },
    { day: '05-16', count: 191 },
    { day: '05-17', count: 184 },
    { day: '05-18', count: 196 },
  ],
  processStatus: [
    { name: '未处理', value: 193 },
    { name: '已处理', value: 1147 },
    { name: '申诉中', value: 48 },
    { name: '已驳回', value: 27 },
  ],
};

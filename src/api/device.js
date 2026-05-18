import { deviceListMock } from '../mock/deviceMock';

export function getDeviceList() {
  return Promise.resolve(deviceListMock);
}

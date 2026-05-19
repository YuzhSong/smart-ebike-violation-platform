import { currentUserMock, userListMock } from '../mock/userMock';

export function getCurrentUser() {
  return Promise.resolve(currentUserMock);
}

export function getUserList() {
  return Promise.resolve(userListMock);
}

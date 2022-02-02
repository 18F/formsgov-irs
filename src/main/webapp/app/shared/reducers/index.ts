import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';
import authentication from './authentication';
import applicationProfile from './application-profile';
import administration from '../../../app/modules/administration/administration.reducer';
import userManagement from './user-management';

const rootReducer = {
  authentication,
  applicationProfile,
  administration,
  userManagement,
  loadingBar,
};

export default rootReducer;

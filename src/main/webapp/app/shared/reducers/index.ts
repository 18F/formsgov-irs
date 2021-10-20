import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';
export interface IRootState {
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  loadingBar
});

export default rootReducer;

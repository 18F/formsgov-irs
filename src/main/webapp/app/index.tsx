import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { AppContainer } from 'react-hot-loader';
import initStore from './config/store';
import AppComponent from './app';
import { loadIcons } from './config/icon-loader';
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
const store = initStore();

loadIcons();
library.add(fas);

const rootEl = document.getElementById('root');

const render = Component =>
  ReactDOM.render(
    <AppContainer>
      <Provider store={store}>
        <div>
          <Component />
        </div>
      </Provider>
    </AppContainer>,
    rootEl
  );

render(AppComponent);

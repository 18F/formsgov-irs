import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import 'app/config/dayjs.ts';

import React, { useEffect } from 'react';
import { Card } from 'reactstrap';
import { BrowserRouter as Router } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from '../app/config/store';
import { getSession } from '../app/shared/reducers/authentication';
import { getProfile } from '../app/shared/reducers/application-profile';
import Header from '../app/shared/layout/header/header';
import Footer from '../app/shared/layout/footer/footer';
import { hasAnyAuthority } from '../app/shared/auth/private-route';
import ErrorBoundary from '../app/shared/error/error-boundary';
import { AUTHORITIES } from '../app/config/constants';
import AppRoutes from '../app/routes';
import HeaderBar from './shared/layout/header/header-bar';

const baseHref = document.querySelector('base').getAttribute('href').replace(/\/$/, '');

export const App = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getSession());
    // dispatch(getProfile());
  }, []);

  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const ribbonEnv = useAppSelector(state => state.applicationProfile.ribbonEnv);
  const isInProduction = useAppSelector(state => state.applicationProfile.inProduction);
  const isOpenAPIEnabled = useAppSelector(state => state.applicationProfile.isOpenAPIEnabled);

  return (
    <Router basename={baseHref}>
      <div className="app-container">
        <ToastContainer position={toast.POSITION.TOP_LEFT} className="toastify-container" toastClassName="toastify-toast" />
        <ErrorBoundary>
          <HeaderBar isAuthenticated={isAuthenticated} isAdmin={isAdmin} isInProduction={isInProduction} />
        </ErrorBoundary>
        <main className="main-content pb-5 pt-3 pl-3" id="main-content" aria-label="Content">
          <div className="grid-container pb-5">
            <div className="grid-row">
              <div className="grid-col-12">
                <ErrorBoundary>
                  <AppRoutes />
                </ErrorBoundary>
              </div>
            </div>
          </div>
        </main>
        <Footer />
      </div>
    </Router>
  );
};
export default App;

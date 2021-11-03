import React from 'react';
import { Switch } from 'react-router-dom';
import Loadable from 'react-loadable';
import LoginRedirect from '../app/modules/login/login-redirect';
import Logout from '../app/modules/login/logout';
import Home from '../app/modules/home/home';
import PrivateRoute from '../app/shared/auth/private-route';
import ErrorBoundaryRoute from '../app/shared/error/error-boundary-route';
import PageNotFound from '../app/shared/error/page-not-found';
import { AUTHORITIES } from '../app/config/constants';
import Forms from './modules/forms/form';
import LogoutSuccess from './modules/login/logout-success';
import SignSuccess from './modules/forms/sign-success';
import SignUnsuccessful from './modules/forms/sign-unsuccessful';
// import SignRequest from './modules/forms/sign-request';
const Admin = Loadable({
  loader: () => import(/* webpackChunkName: "administration" */ '../app/modules/administration'),
  loading: () => <div>loading ...</div>,
});

const Routes = () => {
  return (
    <div className="view-routes">
      <Switch>
        <ErrorBoundaryRoute path="/logout" component={Logout} />
        <PrivateRoute path="/admin" component={Admin} hasAnyAuthorities={[AUTHORITIES.ADMIN]} />
        <ErrorBoundaryRoute path="/sign-unsuccessful" exact component={SignUnsuccessful} />
        <ErrorBoundaryRoute path="/sign-success" exact component={SignSuccess} />
        {/* <ErrorBoundaryRoute path="/sign-request" exact component={SignRequest} /> */}
        <ErrorBoundaryRoute path="/" exact component={Home} />
        <ErrorBoundaryRoute path="/logout-success" exact component={LogoutSuccess} />
        <ErrorBoundaryRoute path="/oauth2/authorization/oidc" component={LoginRedirect} />
        <ErrorBoundaryRoute path="/form/4506-T" component={Forms} />
        <ErrorBoundaryRoute path="/form/8821" component={Forms} />
        <ErrorBoundaryRoute component={PageNotFound} />
      </Switch>
    </div>
  );
};

export default Routes;

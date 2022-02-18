import { Alert, Button } from '@trussworks/react-uswds';
import React from 'react';
import { getLoginUrl } from '../../../app/shared/util/url-utils';

export const LogoutSuccess = () => {
  return (
    <>
      <Alert type="success" heading="Logout Successful" className="mt-5">
        You are logged out successfully!
      </Alert>
      <a className="usa-button sign-in-logo mt-4 mb-4" aria-label="Login.gov" href={getLoginUrl()}>
        Sign in with
        <span>
          <img src="content/images/logingov.png" height="15px" width="15px" alt="login.gov" className="mr-1 ml-1" />
          Login.gov
        </span>
      </a>
    </>
  );
};

export default LogoutSuccess;

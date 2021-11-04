import { Alert, Button } from '@trussworks/react-uswds';
import React from 'react';
import { getLoginUrl } from '../../../app/shared/util/url-utils';

export const LogoutSuccess = () => {
  return (
    <>
      <Alert type="success" heading="Logout Successful" className="mt-5">
        You are logged out successfully!
      </Alert>
      <a href={getLoginUrl()} key="signin" className="usa-nav__link">
        <Button size="big" type="button" className="mt-5 mb-5">
          <span>Sign in using Login.gov</span>
        </Button>
      </a>
    </>
  );
};

export default LogoutSuccess;

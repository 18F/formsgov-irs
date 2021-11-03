import React from 'react';
import { Alert } from 'reactstrap';
import { getLoginUrl } from '../../../app/shared/util/url-utils';

export const LogoutSuccess = () => {
  return (
    <Alert type="success" heading="Logged out success">
      You are logged out successfully! Click here to sign in with
      <span>&nbsp;</span>
      <a href={getLoginUrl()} className="alert-link">
        LOGIN.GOV
      </a>
    </Alert>
  );
};

export default LogoutSuccess;

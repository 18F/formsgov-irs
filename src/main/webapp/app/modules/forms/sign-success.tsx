import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import http from '../../shared/service/http-service';
export const SignSuccess = () => {
  return (
    <div style={{ marginBottom: '18.5rem', marginTop: '3rem' }}>
      <div className="usa-alert usa-alert--success">
        <div className="usa-alert__body">
          <h3 className="usa-alert__heading">Document Signed Successfully</h3>
          <p className="usa-alert__text">
            You have succesfully signed the document. You will receive an email for the document and signing log for your own records. All
            parties involved receive this email.
            <a>
              &nbsp; <Link to="/"> Click here to fill out the form again.</Link>
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};
export default SignSuccess;

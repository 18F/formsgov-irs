import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import http from '../../shared/service/http-service';
export const SignUnsuccessful = () => {
  useEffect(() => {
    const timer = setTimeout(() => {
      sendIrsPayload();
      console.log('This will invoked after 30 second!');
    }, 30000);
    return () => clearTimeout(timer);
  }, []);

  const sendIrsPayload = async () => {
    await http.get('api/send-irs-payload').then(response => {
      /* eslint no-console: off */
      console.log(response);
    });
  };
  return (
    <div style={{ marginBottom: '18.5rem', marginTop: '3rem' }}>
      <div className="usa-alert usa-alert--info">
        <div className="usa-alert__body">
          <h3 className="usa-alert__heading">Document Not Signed</h3>
          <p className="usa-alert__text">
            You have not signed the document,{' '}
            <a>
              <Link to="/">Click here to fill out the form again.</Link>
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};
export default SignUnsuccessful;

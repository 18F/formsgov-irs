import './home.scss';
import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Row, Col, Alert } from 'reactstrap';
import { getLoginUrl, REDIRECT_URL } from '../../../app/shared/util/url-utils';
import { useAppSelector } from '../../../app/config/store';
import { SummaryBox } from '@trussworks/react-uswds';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  });

  return (
    <Row>
      <Col md="9">
        {/* <p className="lead">This is your homepage</p> */}
        {account && account.login ? (
          <div>
            <h2>
              Hello, {account.firstName}&nbsp;{account.lastName}!
            </h2>
            <Alert className="mt-4" color="success">
              You are logged in as user with email {account.email}.
            </Alert>
          </div>
        ) : (
          <>
            <h2>Welcome to FORMS.GOV!</h2>
            <SummaryBox heading="Login Information">
              Please sign here with
              <span>&nbsp;</span>
              <a href={getLoginUrl()} className="alert-link">
                LOGIN.GOV
              </a>
            </SummaryBox>
          </>
        )}
        <br />
        <p>Available forms:</p>
        <ul>
          <li>
            <a href="javascript:void(0);" target="_blank" rel="noopener noreferrer" className="usa-link">
              Form 4506-T
            </a>
          </li>
          <li>
            <a href="javascript:void(0);" target="_blank" rel="noopener noreferrer" className="usa-link">
              Form 8821
            </a>
          </li>
          <li>
            <a href="javascript:void(0);" target="_blank" rel="noopener noreferrer" className="usa-link">
              Form 656
            </a>
          </li>
        </ul>
      </Col>
    </Row>
  );
};

export default Home;

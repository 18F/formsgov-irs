import './home.scss';
import React, { useEffect } from 'react';
import { Row, Col } from 'reactstrap';
import { getLoginUrl, REDIRECT_URL } from '../../../app/shared/util/url-utils';
import { useAppSelector } from '../../../app/config/store';
import { Alert, Button } from '@trussworks/react-uswds';
import { Link } from 'react-router-dom';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  });
  const handleSignIn = () => {};
  return (
    <Row>
      <Col md="9">
        {/* <p className="lead">This is your homepage</p> */}
        {account && account.login ? (
          <>
            <h2>
              Hello, {account.firstName}&nbsp;{account.lastName}!
            </h2>
            <Alert className="mt-5 mb-4" type="success" heading="Login Successful">
              You are logged in with the email address {account.email}.
            </Alert>
            <div>
              <h4 style={{ fontSize: '1.14rem' }}>
                {' '}
                Now that you have signed in, select the form you would like to complete from the drop down menu above labeled Forms. You
                will be able to complete and Sign the form with only a few clicks. Once all the required informaion is entered, you will be
                prompted to sign the foem electronically.
                <br />
                <br />
                If you have any questions about the forms, contact [email].
              </h4>
            </div>
          </>
        ) : (
          <>
            <Alert type="info" heading="Login Information" className="mt-5">
              In order to complete this form you will need to log in using your login.gov account. <br />
              Don&apos;t have a login.gov account?&nbsp;
              <a href="https://login.gov/create-an-account/" target="_blank" rel="noopener noreferrer" className="usa-link">
                Create an account at login.gov.
              </a>
            </Alert>
            <a href={getLoginUrl()} key="signin" className="usa-nav__link">
              <Button onClick={handleSignIn} size="big" type="button" className="mt-5 mb-5">
                <span>Sign in using Login.gov</span>
              </Button>
            </a>
            <h4 style={{ fontSize: '1.14rem' }}>
              After completing the sign in process at login.gov you will be brought back to the IRS forms site.
            </h4>
          </>
        )}
      </Col>
    </Row>
  );
};

export default Home;

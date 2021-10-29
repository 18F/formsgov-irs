import './form.scss';
import React, { useState, useEffect } from 'react';
import { Form } from 'react-formio';
import uswds from '@formio/uswds';
import { Formio } from 'formiojs';
import http from '../../shared/service/http-service';
import { Spinner } from '../../shared/util/utils';
Formio.use(uswds);
export const Forms = () => {
  const [submissionId, setSubmissionId] = useState(0);
  const [jwtToken, setJwtToken] = useState(0);
  const [user, setUser] = useState({});
  const [embedUrl, setEmbedUrl] = useState('');
  const [loader, setLoader] = useState(false);
  const xAllow = `GET:/project/611bd732192fb363527df70d/form/6152179c7e96b7d08f0333bb/submission/${submissionId}/download`;
  useEffect(() => {
    login();
    getUser();
  }, []);

  const requestData = {
    data: {
      email: 'service@gsa.gov',
      password: 'vBEJbMK6DAydFjBitmLbB4ndBhHZpm',
    },
  };
  const login = async () => {
    await http.post('https://portal-test.forms.gov/agencydemo-dev/admin/login', requestData).then(response => {
      setJwtToken(response.headers['x-jwt-token']);
      /* eslint no-console: off */
      console.log(response.status);
    });
  };

  const getUser = async () => {
    const { data: response } = await http.get('api/user');
    /* eslint no-console: off */
    console.log(response);
    setUser(response);
  };

  const getATokenKeyAndSign = async () => {
    console.log('jwtToken **** ' + jwtToken);
    await http
      .get('https://portal-test.forms.gov/agencydemo-dev/token', {
        headers: {
          'x-jwt-token': jwtToken,
          'x-allow': xAllow,
          'x-expire': 3600,
        },
      })
      .then(resp => {
        console.log('Key ***** ' + resp.data['key']);
        getSignedRequest(resp.data['key']);
      });
  };
  const handleOnSubmitDone = e => {
    setLoader(true);
    /* eslint no-console: off */
    console.log('handleOnSubmitDone ' + e);
    getATokenKeyAndSign();
  };
  const handleOnSubmit = event => {
    setLoader(true);
    setSubmissionId(event._id);
    /* eslint no-console: off */
    console.log('handleOnSubmit ', event);
  };

  const getSignedRequest = async key => {
    const pdfUrl =
      'https://portal-test.forms.gov/agencydemo-dev/form/6152179c7e96b7d08f0333bb/submission/' + submissionId + '/download?token=' + key;
    const pdfName = '4506t.pdf';
    const { data: response } = await http.get('api/sign', {
      params: {
        pdfUrl,
        pdfName,
      },
    });
    /* eslint no-console: off */
    console.log('response ***** ' + JSON.stringify(response));
    const embed_url = response.signers[0].embed_url;
    setEmbedUrl(embed_url);
    setLoader(false);
  };

  const phone = user['phone'];
  const formattedPhone = phone !== undefined ? phone.replace(/\D+/g, '').replace(/^(\d{3})(\d{3})(\d{4}).*/, '($1) $2-$3') : '';
  const submissionData = {
    data: {
      name1: user['firstName'] + ' ' + user['lastName'],
      // taxpayerLastName: user['lastName'],
      currentNameAndAddress: user['address1'] + ', ' + user['city'] + ', ' + user['state'] + ', ' + user['zipcode'],
      // taxpayerCity: user['city'],
      // taxpayerZip: user['zipcode'],
      // taxpayerState: user['state'],
      // taxpayerDaytimePhoneNumber: formattedPhone,
      socialSecurityNumber: user['ssn'],
    },
  };
  return (
    <div className="ds-u-padding--2" style={{ position: 'relative' }}>
      {embedUrl === '' ? (
        <Form
          src="https://portal-test.forms.gov/agencydemo-dev/4506t"
          onSubmitDone={handleOnSubmitDone}
          onSubmit={handleOnSubmit}
          submission={submissionData}
        />
      ) : (
        (window.location.href = embedUrl)
      )}
      {loader && <Spinner />}
    </div>
  );
};
export default Forms;

import './form.scss';
import React, { useState, useEffect } from 'react';
import { Form } from 'react-formio';
import uswds from '@formio/uswds';
import { Formio } from 'formiojs';
import http from '../../shared/service/http-service';
import { useAppSelector } from '../../../app/config/store';
import LoadingOverlay from 'react-loading-overlay';
import { FadeLoader } from 'react-spinners';
import { useLocation } from 'react-router-dom';
Formio.use(uswds);
export const Forms = () => {
  const [submissionId, setSubmissionId] = useState(0);
  const [jwtToken, setJwtToken] = useState(0);
  const [user, setUser] = useState({});
  const [embedUrl, setEmbedUrl] = useState('');
  const [loader, setLoader] = useState(true);
  const account = useAppSelector(state => state.authentication.account);
  const location = useLocation();
  // dev stage
  const formioDevEnvId = '611bd732192fb363527df70d';
  const formDevId =
    location.pathname === '/form/12153'
      ? '619032a70781a17f63a71edb'
      : location.pathname === '/form/12203'
      ? '6195555c3bd148bde37a4ad1'
      : '';
  // test stage
  const formioTestEnvId = '61fd56e597a95f40a9f921cd';
  const formTestId =
    location.pathname === '/form/12153'
      ? '6201967697a95f40a9fafbbc'
      : location.pathname === '/form/12203'
      ? '6201967697a95f40a9fafbb4'
      : '';
  const formSrcDev =
    location.pathname === '/form/12153'
      ? 'https://portal-test.forms.gov/agencydemo-dev/irsform12153'
      : location.pathname === '/form/12203'
      ? 'https://portal-test.forms.gov/agencydemo-dev/irsform12203'
      : '';
  const formSrcTest =
    location.pathname === '/form/12153'
      ? 'https://portal-test.forms.gov/irs-dev/irsform12153'
      : location.pathname === '/form/12203'
      ? 'https://portal-test.forms.gov/irs-dev/irsform12203'
      : '';
  let formData;
  const formioEnv = 'irs-dev';

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
    await http.post(`https://portal-test.forms.gov/${formioEnv}/admin/login`, requestData).then(response => {
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
    const xAllow = `GET:/project/${formioTestEnvId}/form/${formTestId}/submission/${submissionId}/download`;
    await http
      .get(`https://portal-test.forms.gov/${formioEnv}/token`, {
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
    /* eslint no-console: off */
    console.log('handleOnSubmitDone ' + JSON.stringify(e.data));
    formData = e.data;
    getATokenKeyAndSign();
    setLoader(false);
  };
  const handleOnSubmit = event => {
    setSubmissionId(event._id);
    setLoader(true);
    /* eslint no-console: off */
    console.log('handleOnSubmit ', event);
  };

  const getSignedRequest = async key => {
    const form = location.pathname.substring(6);
    const pdfUrl = `https://portal-test.forms.gov/${formioEnv}/form/${formTestId}/submission/${submissionId}/download?token=${key}`;
    const pdfName = `${form}.pdf`;
    const taxpayerName2 = formData.taxpayerName2 ? formData.taxpayerName2 : '';
    const taxpayer2Email = formData.taxpayer2Email ? formData.taxpayer2Email : '';
    const jointRequest = formData.jointRequest;
    const { data: response } = await http.get('api/sign', {
      params: {
        pdfUrl,
        pdfName,
        taxpayerName2,
        taxpayerLastName2: '',
        taxpayer2Email,
        jointRequest,
      },
    });
    /* eslint no-console: off */
    console.log('response ***** ' + JSON.stringify(response));
    const embed_url = response.signers[0].embed_url;
    setEmbedUrl(embed_url);
  };

  const handelOnFormReady = () => {
    setLoader(submissionId !== 0);
  };

  const phone = user['phone'];
  const formattedPhone = phone !== undefined ? phone.replace(/\D+/g, '').replace(/^(\d{3})(\d{3})(\d{4}).*/, '($1) $2-$3') : '';
  const form12153 = location.pathname === '/form/12153';
  const submissionData = {
    data: {
      taxpayerName1: account.firstName + ' ' + account.lastName,
      // taxpayerLastName: user['lastName'],
      // ...(form12153 ? { currentaddress1: account.formatted, } : { mailingAddress: account.formatted }),
      // currentNameAndAddress: account.firstName + ' ' + account.lastName + ' , ' + account.formatted,
      // city1: user['city'],
      // zipCode1: user['zipcode'],
      // state1: user['state'],
      // phoneNumber1: formattedPhone,
      // socialSecurityNumber: account.ssn,
    },
  };
  return (
    <LoadingOverlay
      active={loader}
      styles={{ overlay: base => ({ ...base, background: 'rgba(0, 0, 0, 0.1)' }) }}
      spinner={<FadeLoader color={'#4A90E2'} />}
    >
      {embedUrl === '' ? (
        <Form
          src={formSrcTest}
          onSubmitDone={handleOnSubmitDone}
          onSubmit={handleOnSubmit}
          submission={submissionData}
          onRender={handelOnFormReady}
        />
      ) : (
        (window.location.href = embedUrl)
      )}
    </LoadingOverlay>
  );
};
export default Forms;

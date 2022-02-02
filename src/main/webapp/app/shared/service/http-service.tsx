import axios from 'axios';
import * as React from 'react';
import { toast } from 'react-toastify';

// let showModal = false;
const TIMEOUT = 1 * 90 * 1000;
const title = <div style={{ color: 'red' }}>Unexpected internal system error has occurred</div>;
const message = <div>Unable to process your request at the moment.</div>;

const errorMessage = <div>Internal server error occured !.</div>;
axios.defaults.timeout = TIMEOUT;
axios.defaults.headers = { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' };
if (process.env.NODE_ENV === 'development') {
  axios.defaults.baseURL = 'https://localhost:8181/';
}
axios.interceptors.response.use(null, error => {
  console.error('Interceptors Error ', error);
  const expectedError = error.response && error.response.status && error.response.status === 401;
  if (error.config.url === 'api/account' && expectedError) {
    console.error('Unauthorized User', error, error.response.status);
  } else {
    toast.error(errorMessage, {
      position: toast.POSITION.TOP_LEFT,
      autoClose: 12000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
    });
    return Promise.reject(error);
  }
});
export default {
  get: axios.get,
  post: axios.post,
  put: axios.put,
  delete: axios.delete,
};

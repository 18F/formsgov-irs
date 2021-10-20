import './home.scss';
import React from 'react';
import uswds from '@formio/uswds';
import { Formio } from 'formiojs';

import SBAFORM from '../forms/sba4506t';
Formio.use(uswds);
export const Home = () => {
  return <SBAFORM />;
};
export default Home;

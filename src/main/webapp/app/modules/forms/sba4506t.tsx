import './sba4506t.scss';
import React from 'react';
import { Form } from 'react-formio';
import uswds from '@formio/uswds';
import { Formio } from 'formiojs';
Formio.use(uswds);
const SBA = () => (
  <div>
    <Form src="https://portal-test.forms.gov/agencydemo-dev/4506t" />
  </div>
);
export default SBA;

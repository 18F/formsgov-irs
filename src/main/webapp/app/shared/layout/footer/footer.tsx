import './footer.scss';
import React from 'react';
import { Logo } from '@trussworks/react-uswds';
const Footer = () => (
  <div className="usa-footer__secondary-section footer">
    <Logo image={<img className="footer-logo-img" src="content/images/Formsgov_50px.png" alt="forms.gov" />} />
  </div>
);
export default Footer;

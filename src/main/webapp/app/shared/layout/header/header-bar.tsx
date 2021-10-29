import './header.scss';
import React, { useState } from 'react';
import { getLoginUrl } from '../../../../app/shared/util/url-utils';
import { GovBanner, Header, Menu, NavDropDownButton, NavList, NavMenuButton, PrimaryNav, Title } from '@trussworks/react-uswds';
import { Link } from 'react-router-dom';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  isInProduction: boolean;
}

const HeaderBar = (props: IHeaderProps) => {
  const [expanded, setExpanded] = useState(false);
  const onClick = (): void => setExpanded(prvExpanded => !prvExpanded);
  const [isOpen, setIsOpen] = useState([false, false]);
  const formsMenuItems = [
    <Link to="/form/4506-T" key="one">
      form-4506T
    </Link>,
    <Link to="/8821" key="two">
      form-8821
    </Link>,
  ];

  const onToggle = (index: number): void => {
    setIsOpen(prevIsOpen => {
      const newIsOpen = [false, false];
      newIsOpen[index] = !prevIsOpen[index];
      return newIsOpen;
    });
  };

  const formsItemsMenu = [
    <Link to="/" key="home" className="usa-nav__link">
      <span>Home</span>
    </Link>,
    <>
      {props.isAuthenticated && (
        <>
          <NavDropDownButton
            menuId="formsDropDownOne"
            onToggle={(): void => {
              onToggle(0);
            }}
            isOpen={isOpen[0]}
            label="Forms"
            isCurrent={true}
          />
          <Menu key="one" items={formsMenuItems} isOpen={isOpen[0]} id="formsDropDownOne" />
        </>
      )}
    </>,
    <>
      {' '}
      {!props.isAuthenticated ? (
        <a href={getLoginUrl()} key="signin" className="usa-nav__link">
          <span>Sign in</span>
        </a>
      ) : (
        <a href="/logout" key="signout" className="usa-nav__link">
          <span>Sign out</span>
        </a>
      )}
    </>,
  ];

  return (
    <>
      <GovBanner aria-label="Official government website" />
      <div className={`usa-overlay ${expanded ? 'is-visible' : ''}`}></div>
      <Header basic={true}>
        <div className="usa-nav-container">
          <div className="usa-navbar">
            <Title>
              <Link to="/" key="jome">
                <img src="content/images/irs-logo.png" alt="IRS logo" />
              </Link>
            </Title>
            <NavMenuButton onClick={onClick} label="Menu" />
          </div>
          <PrimaryNav className="pb-4" items={formsItemsMenu} mobileExpanded={expanded} onToggleMobileNav={onClick}></PrimaryNav>
        </div>
      </Header>
    </>
  );
};
export default HeaderBar;

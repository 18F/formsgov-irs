import './header.scss';
import React, { useState } from 'react';
import { NavLink as Link, NavLink } from 'react-router-dom';
import LoadingBar from 'react-redux-loading-bar';
import { getLoginUrl } from '../../../../app/shared/util/url-utils';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  isInProduction: boolean;
}

const Header = (props: IHeaderProps) => {
  return (
    <div id="app-header">
      <LoadingBar className="loading-bar" />
      <a className="usa-skipnav" href="#main-content">
        Skip to main content
      </a>
      <div className="usa-banner" aria-label="Official government website">
        <div className="usa-accordion">
          <header className="usa-banner__header">
            <div className="usa-banner__inner">
              <div className="grid-col-auto">
                <img className="usa-banner__header-flag" src="content/images/us_flag_small.png" alt="U.S. flag" />
              </div>
              <div className="grid-col-fill tablet:grid-col-auto">
                <p className="usa-banner__header-text">An official website of the United States government</p>
                <p className="usa-banner__header-action" aria-hidden="true">
                  Here’s how you know
                </p>
              </div>
              <button className="usa-accordion__button usa-banner__button" aria-expanded="false" aria-controls="gov-banner">
                <span className="usa-banner__button-text">Here’s how you know</span>
              </button>
            </div>
          </header>
          <div className="usa-banner__content usa-accordion__content" id="gov-banner" hidden>
            <div className="grid-row grid-gap-lg">
              <div className="usa-banner__guidance tablet:grid-col-6">
                <img className="usa-banner__icon usa-media-block__img" src="content/images/icon-dot-gov.svg" alt="Dot gov" />
                <div className="usa-media-block__body">
                  <p>
                    <strong>Official websites use .gov</strong>
                    <br />A <strong>.gov</strong> website belongs to an official government organization in the United States.
                  </p>
                </div>
              </div>
              <div className="usa-banner__guidance tablet:grid-col-6">
                <img className="usa-banner__icon usa-media-block__img" src="content/images/icon-https.svg" alt="Https" />
                <div className="usa-media-block__body">
                  <p>
                    <strong>Secure .gov websites use HTTPS</strong>
                    <br />A <strong>lock</strong> (
                    <span />
                    <img src="content/images/lock.png" alt="A locked padlock" />) or <strong>https://</strong> means you’ve safely connected
                    to the .gov website. Share sensitive information only on official, secure websites.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className="usa-overlay" />
      <header className="usa-header usa-header--basic">
        <div className="usa-nav-container mt-4 mb-3">
          <div className="logo">
            <Link to="/">
              <img src="content/images/irs-logo.png" alt="IRS logo" />
            </Link>
            <span className="usa-sr-only">Home</span>
            <button className="usa-menu-btn float-right m-2">Menu</button>
          </div>
          <nav aria-label="Primary navigation" className="usa-nav" role="navigation">
            <button className="usa-nav__close">
              <img src="content/images/close.svg" alt="close" />
            </button>
            <ul className="usa-nav__primary usa-accordion">
              <li className="usa-nav__primary-item">
                <NavLink exact activeClassName="usa-current" to="/">
                  <span>Home</span>
                </NavLink>
              </li>
              {props.isAuthenticated && (
                <li className="usa-nav__primary-item">
                  <button className="usa-accordion__button usa-nav__link" aria-expanded="false" aria-controls="nav-2">
                    <span>Forms</span>
                  </button>
                  <ul id="nav-2" className="usa-nav__submenu">
                    <li className="usa-nav__submenu-item">
                      <Link to="/4506-T">
                        <span>4506-T</span>
                      </Link>
                    </li>
                    <li className="usa-nav__submenu-item">
                      <Link to="/">
                        <span>MTW</span>
                      </Link>
                    </li>
                  </ul>
                </li>
              )}
              {props.isAuthenticated ? (
                <li className="usa-nav__primary-item">
                  <NavLink to="logout">
                    <span>Sign out</span>
                  </NavLink>
                </li>
              ) : (
                <li className="usa-nav__primary-item">
                  <a href={getLoginUrl()}>
                    <span>Sign in</span>
                  </a>
                </li>
              )}
            </ul>
          </nav>
        </div>
      </header>
    </div>
  );
};

export default Header;

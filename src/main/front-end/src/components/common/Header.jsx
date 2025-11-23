import React from 'react';
import PropTypes from 'prop-types';
import './Header.css';

export default function Header({ title }) {
    return (
        <header className="header">
            <img src="../../../src/assets/STE-icon.png" alt="Sophia Tech Eats Logo" className="header-logo" />
            <h1>{title}</h1>
        </header>
    );
}

Header.propTypes = {
    title: PropTypes.string.isRequired
};

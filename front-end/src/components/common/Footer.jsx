import React from 'react';

export default function Footer() {
    return (
        <footer className="mt-auto w-full bg-white border-t border-neutral-200 py-6">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
                    <p className="text-sm text-neutral-600">
                        © 2025 Sophia Tech Eats App. Tous droits réservés.
                    </p>
                    <div className="flex items-center gap-6 text-sm text-neutral-600">
                        <a href="#" className="hover:text-primary-600 transition-colors">
                            À propos
                        </a>
                        <a href="#" className="hover:text-primary-600 transition-colors">
                            Contact
                        </a>
                        <a href="#" className="hover:text-primary-600 transition-colors">
                            Confidentialité
                        </a>
                    </div>
                </div>
            </div>
        </footer>
    );
}

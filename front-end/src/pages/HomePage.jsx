import React from "react";
import { useNavigate } from "react-router-dom";
import BaseLayout from "../components/layouts/BaseLayout.jsx";
import { UserGroupIcon, BuildingStorefrontIcon } from '@heroicons/react/24/outline';

export default function HomePage() {
    const navigate = useNavigate();

    return (
        <BaseLayout title="Sophia Tech Eats">
            <div className="flex-1 flex items-center justify-center px-4 py-12">
                <div className="max-w-4xl w-full text-center space-y-8">
                    <div className="space-y-4">
                        <h2 className="text-4xl font-bold text-neutral-900">
                            Bienvenue sur Sophia Tech Eats
                        </h2>
                        <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
                            Commandez vos repas préférés sur le campus en quelques clics
                        </p>
                    </div>

                    <div className="grid md:grid-cols-2 gap-6 max-w-3xl mx-auto pt-8">
                        {/* User Interface Button */}
                        <button
                            onClick={() => navigate("/restaurants")}
                            className="group relative bg-white p-8 rounded-apple-lg border-2 border-neutral-200
                                     hover:border-primary-500 shadow-apple hover:shadow-apple-lg
                                     transition-all duration-300 hover:-translate-y-1"
                        >
                            <div className="space-y-4">
                                <div className="flex justify-center">
                                    <div className="p-4 bg-primary-50 rounded-apple-lg group-hover:bg-primary-100
                                                  transition-colors">
                                        <UserGroupIcon className="w-12 h-12 text-primary-600" />
                                    </div>
                                </div>
                                <div>
                                    <h3 className="text-xl font-semibold text-neutral-900 mb-2">
                                        Interface Utilisateur
                                    </h3>
                                    <p className="text-sm text-neutral-600">
                                        Parcourir les restaurants et passer commande
                                    </p>
                                </div>
                            </div>
                        </button>

                        {/* Manager Interface Button */}
                        <button
                            onClick={() => navigate("/manager/restaurants")}
                            className="group relative bg-white p-8 rounded-apple-lg border-2 border-neutral-200
                                     hover:border-success shadow-apple hover:shadow-apple-lg
                                     transition-all duration-300 hover:-translate-y-1"
                        >
                            <div className="space-y-4">
                                <div className="flex justify-center">
                                    <div className="p-4 bg-success-light rounded-apple-lg group-hover:bg-success-light/80
                                                  transition-colors">
                                        <BuildingStorefrontIcon className="w-12 h-12 text-success-dark" />
                                    </div>
                                </div>
                                <div>
                                    <h3 className="text-xl font-semibold text-neutral-900 mb-2">
                                        Interface Restaurant
                                    </h3>
                                    <p className="text-sm text-neutral-600">
                                        Gérer votre restaurant et vos plats
                                    </p>
                                </div>
                            </div>
                        </button>
                    </div>
                </div>
            </div>
        </BaseLayout>
    );
}

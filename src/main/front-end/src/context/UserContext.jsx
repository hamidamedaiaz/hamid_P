import { createContext, useContext, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    // Génération dynamique d'un userId unique pour chaque session
    // Dans une vraie app, ce serait récupéré depuis l'authentification
    const [userId, setUserId] = useState(() => {
        // Récupérer l'userId depuis le localStorage s'il existe
        const storedUserId = localStorage.getItem('userId');
        if (storedUserId) {
            return storedUserId;
        }
        // Sinon, en générer un nouveau
        const newUserId = uuidv4();
        localStorage.setItem('userId', newUserId);
        return newUserId;
    });

    const [userName] = useState("Utilisateur");

    return (
        <UserContext.Provider value={{ userId, userName }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    const context = useContext(UserContext);
    if (!context) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return context;
};

export default UserContext;

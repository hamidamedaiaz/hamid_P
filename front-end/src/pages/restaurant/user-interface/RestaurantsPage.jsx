import React from 'react';
import UserLayout from '../../../components/layouts/UserLayout.jsx';
import PageContainer from '../../../components/common/PageContainer.jsx';
import RestaurantList from '../../../components/restaurant/RestaurantList.jsx';

export default function RestaurantsPage() {
    return (
        <UserLayout
            title="Nos Restaurants"
            showBackButton={true}
            backTo="/"
            showActionBar={true}
        >
            <PageContainer>
                <RestaurantList />
            </PageContainer>
        </UserLayout>
    );
}

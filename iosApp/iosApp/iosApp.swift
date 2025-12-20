import SwiftUI
import composeApp

@main
struct iosApp: App {
    
    init() {
        MainViewControllerKt.doInitKoinIos()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
